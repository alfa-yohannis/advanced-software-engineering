'''
Docstring for shared.crypto
Run this to generate a key and save it in .env, next to docker-compose.yml:

python -c "from cryptography.fernet import Fernet; print('PAYLOAD_KEY=' + Fernet.generate_key().decode())" > .env

Git ignores .env. docker-compose.yml passes the key to every node that
encrypts or decrypts, and not to the broker. For example:

services:
 ...
 transformer:
    build:
      context: .
      dockerfile: transformer/Dockerfile
    ...
    environment:
      PAYLOAD_KEY: ${PAYLOAD_KEY}
'''

import os
from cryptography.fernet import Fernet

_KEY_ENV = "PAYLOAD_KEY"

def _get_key() -> bytes:
    k = os.getenv(_KEY_ENV, "").encode()
    if not k:
        raise RuntimeError(f"Missing env var {_KEY_ENV}. Put it in .env next to docker-compose.yml. Generate with: python -c \"from cryptography.fernet import Fernet; print(Fernet.generate_key().decode())\"")
    return k

# Read the key once, when the node starts, so a missing or malformed key stops
# the node right away instead of failing on every message
_cipher = Fernet(_get_key())

def encrypt_bytes(data: bytes) -> bytes:
    return _cipher.encrypt(data)

def decrypt_bytes(token: bytes) -> bytes:
    return _cipher.decrypt(token)
