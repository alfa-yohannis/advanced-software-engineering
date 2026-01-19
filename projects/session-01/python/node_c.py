# node_c.py
import os, time, json, io
import zmq
from PIL import Image

BROKER_IP = "127.0.0.1"  # set to broker machine IP
SUB_ENDPOINT = f"tcp://{BROKER_IP}:5556"

SUB_TOPIC = b"processed"
OUT_DIR = "."

def jpeg_bytes_to_pil(b: bytes) -> Image.Image:
    return Image.open(io.BytesIO(b))

def main(save_every_n: int = 1, show_tk: bool = False):
    os.makedirs(OUT_DIR, exist_ok=True)

    ctx = zmq.Context.instance()
    sub = ctx.socket(zmq.SUB)
    sub.connect(SUB_ENDPOINT)
    sub.setsockopt(zmq.SUBSCRIBE, SUB_TOPIC)

    print(f"Node C subscribed to {SUB_ENDPOINT} topic={SUB_TOPIC!r}")
    print(f"Saving frames to: {OUT_DIR}/")

    last_log = time.time()
    frames = 0

    # Optional Tkinter live view
    if show_tk:
        import tkinter as tk
        from PIL import ImageTk
        root = tk.Tk()
        root.title("Node C - Processed Stream")
        label = tk.Label(root)
        label.pack()
        tk_img = None

    while True:
        topic, header_b, jpeg_in = sub.recv_multipart()
        header = json.loads(header_b.decode("utf-8"))
        img = jpeg_bytes_to_pil(jpeg_in)

        frames += 1
        if (header["frame_id"] % save_every_n) == 0:
            out_path = os.path.join(OUT_DIR, f"frame_{header['frame_id']:06d}.jpg")
            with open(out_path, "wb") as f:
                f.write(jpeg_in)

        # Update Tkinter preview if enabled
        if show_tk:
            from PIL import ImageTk
            # Convert grayscale 'L' to RGB for Tk if needed
            view = img.convert("RGB")
            tk_img = ImageTk.PhotoImage(view)
            label.configure(image=tk_img)
            root.update_idletasks()
            root.update()

        now = time.time()
        if now - last_log >= 1.0:
            print(f"FPS≈{frames/(now-last_log):.1f}  last_frame={header['frame_id']}  size={header['w']}x{header['h']} mode={header.get('mode')}")
            frames = 0
            last_log = now

if __name__ == "__main__":
    main(save_every_n=1, show_tk=False)  # set show_tk=True to view
