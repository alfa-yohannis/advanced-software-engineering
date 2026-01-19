# node_a.py
import time, json
import zmq
import cv2

BROKER_IP = "127.0.0.1"   # set to broker machine IP
PUB_ENDPOINT = f"tcp://{BROKER_IP}:5555"

TOPIC = b"raw"

def main(
    video_path: str = "input.mp4",
    max_fps: float | None = 10.0,   # cap FPS (set None to use video FPS)
    jpeg_quality: int = 85,
    loop: bool = True,
):
    ctx = zmq.Context.instance()
    pub = ctx.socket(zmq.PUB)
    pub.connect(PUB_ENDPOINT)

    cap = cv2.VideoCapture(video_path)
    if not cap.isOpened():
        raise RuntimeError(f"Cannot open video: {video_path}")

    video_fps = cap.get(cv2.CAP_PROP_FPS)
    if not video_fps or video_fps <= 0:
        video_fps = 25.0  # fallback

    fps = min(video_fps, max_fps) if max_fps else video_fps
    period = 1.0 / max(fps, 0.1)

    encode_params = [
        int(cv2.IMWRITE_JPEG_QUALITY),
        int(jpeg_quality)
    ]

    frame_id = 0
    time.sleep(0.5)  # PUB/SUB slow-joiner mitigation

    print(f"Node A publishing video {video_path}")
    print(f"  video_fps={video_fps:.2f}, target_fps={fps:.2f}, jpeg_quality={jpeg_quality}")

    while True:
        t0 = time.time()

        ok, frame_bgr = cap.read()
        if not ok:
            if loop:
                cap.set(cv2.CAP_PROP_POS_FRAMES, 0)
                continue
            else:
                print("End of video.")
                break

        h, w = frame_bgr.shape[:2]

        ok, jpg = cv2.imencode(".jpg", frame_bgr, encode_params)
        if not ok:
            continue

        header = {
            "frame_id": frame_id,
            "ts": t0,
            "encoding": "jpeg",
            "mode": "BGR",
            "w": int(w),
            "h": int(h),
            "src": "video",
            "video": video_path,
            "video_fps": float(video_fps),
            "target_fps": float(fps),
        }

        pub.send_multipart([
            TOPIC,
            json.dumps(header).encode("utf-8"),
            jpg.tobytes()
        ])

        frame_id += 1

        # FPS pacing
        elapsed = time.time() - t0
        if elapsed < period:
            time.sleep(period - elapsed)

    cap.release()

if __name__ == "__main__":
    main(
        video_path="input.mp4",
        max_fps=10.0,        # CCTV-like default
        jpeg_quality=80,
        loop=True
    )
