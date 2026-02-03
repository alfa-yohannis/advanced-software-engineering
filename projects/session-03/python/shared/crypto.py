'''
Docstring for shared.crypto
Run this to generate a key:

python -c "from cryptography.fernet import Fernet; print(Fernet.generate_key().decode())"

and copy paste it into every node that you want to use encryption. For example:

services:
 ...
 transformer:
    build:
      context: .
      dockerfile: transformer/Dockerfile
    ...
    environment:
    - PAYLOAD_KEY=RmTnzrS0_DwulCzF6tj8qSOQpCmnOkRmKeezZcZA4T4=
'''

import os
from cryptography.fernet import Fernet

_KEY_ENV = "PAYLOAD_KEY"

def _get_key() -> bytes:
    k = os.getenv(_KEY_ENV, "").encode()
    if not k:
        raise RuntimeError(f"Missing env var {_KEY_ENV}. Generate with: python -c \"from cryptography.fernet import Fernet; print(Fernet.generate_key().decode())\"")
    return k

def encrypt_bytes(data: bytes) -> bytes:
    return Fernet(_get_key()).encrypt(data)

def decrypt_bytes(token: bytes) -> bytes:
    return Fernet(_get_key()).decrypt(token)
