# node_a.py (hybrid: play video normally, publish every N seconds)
import time, json
import zmq
import cv2

BROKER_IP = "127.0.0.1"
PUB_ENDPOINT = f"tcp://{BROKER_IP}:5555"
TOPIC = b"raw"

def main(
    video_path="input.mp4",
    publish_every_sec=2.0,   # <-- capture/publish interval (N seconds)
    jpeg_quality=80,
    loop=True,
):
    ctx = zmq.Context.instance()
    pub = ctx.socket(zmq.PUB)
    pub.connect(PUB_ENDPOINT)

    cap = cv2.VideoCapture(video_path)
    if not cap.isOpened():
        raise RuntimeError(f"Cannot open {video_path}")

    video_fps = cap.get(cv2.CAP_PROP_FPS)
    if not video_fps or video_fps <= 0:
        video_fps = 25.0

    period = 1.0 / video_fps  # <-- playback speed (normal)
    encode_params = [cv2.IMWRITE_JPEG_QUALITY, int(jpeg_quality)]

    frame_id = 0
    next_publish_time = time.time()  # wall-clock schedule
    time.sleep(0.5)  # PUB/SUB slow joiner mitigation

    print(f"Playing {video_path} at ~{video_fps:.2f} FPS")
    print(f"Publishing 1 frame every {publish_every_sec:.2f} seconds to {PUB_ENDPOINT}")

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

        # video plays normally because we keep reading frames at video FPS

        now = time.time()
        if now >= next_publish_time:
            h, w = frame_bgr.shape[:2]
            ok, jpg = cv2.imencode(".jpg", frame_bgr, encode_params)
            if ok:
                header = {
                    "frame_id": frame_id,
                    "ts": now,
                    "encoding": "jpeg",
                    "mode": "BGR",
                    "w": int(w),
                    "h": int(h),
                    "src": "video",
                    "video": video_path,
                    "video_fps": float(video_fps),
                    "publish_every_sec": float(publish_every_sec),
                }
                pub.send_multipart([TOPIC, json.dumps(header).encode("utf-8"), jpg.tobytes()])
                frame_id += 1

            # schedule next capture
            next_publish_time += publish_every_sec

            # if we fell behind (e.g., pause), jump forward to avoid burst publishes
            if now - next_publish_time > publish_every_sec * 5:
                next_publish_time = now + publish_every_sec

        # playback pacing (normal speed)
        elapsed = time.time() - t0
        if elapsed < period:
            time.sleep(period - elapsed)

    cap.release()

if __name__ == "__main__":
    main(
        video_path="input.mp4",
        publish_every_sec=0.1,  # change to N seconds
        jpeg_quality=80,
        loop=True,
    )
