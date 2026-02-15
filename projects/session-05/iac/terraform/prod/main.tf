module "tts" {
  source = "../modules/tts_stack"

  env          = "production"
  project_root = abspath("${path.module}/../../..")

  # ✅ dedicated production ports (no collision with dev/staging)
  ports = {
    redis         = 26379
    minio_api     = 29000
    minio_console = 29001
    api           = 28000
    web           = 28080
    gateway       = 28088
  }

  mp3_bucket     = var.mp3_bucket
  internal_token = var.internal_token

  # ❗ DO NOT hardcode in real production
  minio_root_user     = var.minio_root_user
  minio_root_password = var.minio_root_password

  piper_model_config_path = "/models/en_GB-alan-medium.onnx.json"
  piper_length_scale      = "1.0"

  # 🚀 higher concurrency in production
  worker_replicas = var.worker_replicas

  # Usually false in production (models baked in image)
  mount_models = var.mount_models
}

variable "mp3_bucket" {
  type = string
}

variable "internal_token" {
  type      = string
  sensitive = true
}

variable "worker_replicas" {
  type    = number
  default = 4
}

variable "mount_models" {
  type    = bool
  default = false
}

variable "minio_root_user" {
  type      = string
  sensitive = true
}

variable "minio_root_password" {
  type      = string
  sensitive = true
}

output "urls" {
  value = module.tts.urls
}
