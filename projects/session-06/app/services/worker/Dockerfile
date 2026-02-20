FROM python:3.12.3-slim

RUN apt-get update && apt-get install -y --no-install-recommends \
    ffmpeg ca-certificates curl tar \
 && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Install Piper (binary + bundled .so deps) from tar.gz
RUN set -eux; \
    curl -L -o /tmp/piper.tar.gz \
      https://github.com/rhasspy/piper/releases/download/2023.11.14-2/piper_linux_x86_64.tar.gz; \
    mkdir -p /tmp/piper_extracted; \
    tar -xzf /tmp/piper.tar.gz -C /tmp/piper_extracted; \
    rm -f /tmp/piper.tar.gz; \
    \
    # The tar extracts into a top-level "piper/" directory (as in your tree)
    test -d /tmp/piper_extracted/piper; \
    \
    # Install whole folder (includes piper binary + libpiper_phonemize.so.1 + onnxruntime + espeak-ng-data)
    rm -rf /opt/piper; \
    mv /tmp/piper_extracted/piper /opt/piper; \
    rm -rf /tmp/piper_extracted; \
    \
    # Sanity checks based on your tree layout
    test -x /opt/piper/piper; \
    test -f /opt/piper/libpiper_phonemize.so.1; \
    test -f /opt/piper/libonnxruntime.so.1.14.1; \
    test -d /opt/piper/espeak-ng-data; \
    \
    # Make piper callable globally
    ln -sf /opt/piper/piper /usr/local/bin/piper; \
    \
    # Optional: show linked libs in build logs (won't fail build)
    ldd /opt/piper/piper || true

# IMPORTANT: libs are in /opt/piper (same directory as binary)
ENV LD_LIBRARY_PATH="/opt/piper:${LD_LIBRARY_PATH}"

# (Optional but helpful) point to bundled espeak-ng data
ENV ESPEAK_DATA_PATH="/opt/piper/espeak-ng-data"

# Download your HF model
RUN mkdir -p /models && \
    curl -L -o /models/en_GB-alan-medium.onnx \
    https://huggingface.co/rhasspy/piper-voices/resolve/main/en/en_GB/alan/medium/en_GB-alan-medium.onnx && \
    curl -L -o /models/en_GB-alan-medium.onnx.json \
    https://huggingface.co/rhasspy/piper-voices/resolve/main/en/en_GB/alan/medium/en_GB-alan-medium.onnx.json

ENV PIPER_MODEL_PATH=/models/en_GB-alan-medium.onnx
ENV PIPER_MODEL_CONFIG_PATH=/models/en_GB-alan-medium.onnx.json

COPY worker.py .

CMD ["python", "worker.py"]
