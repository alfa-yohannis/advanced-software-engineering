module "tts" {
  source = "../modules/tts_stack"

  env          = "staging"
  project_root = abspath("${path.module}/../../..")

  # ✅ shifted ports (no collision with dev)
  ports = {
    redis         = 16379
    minio_api     = 19000
    minio_console = 19001
    api           = 18000
    web           = 18080
    gateway       = 18088
  }

  mp3_bucket     = var.mp3_bucket
  internal_token = var.internal_token

  minio_root_user     = "minioadmin"
  minio_root_password = "minioadmin"

  piper_model_config_path = "/models/en_GB-alan-medium.onnx.json"
  piper_length_scale      = "1.0"

  worker_replicas = var.worker_replicas
  mount_models    = var.mount_models
}

variable "mp3_bucket" {
  type = string
}

variable "internal_token" {
  type = string
}

variable "worker_replicas" {
  type    = number
  default = 2
}

variable "mount_models" {
  type    = bool
  default = true
}

output "urls" {
  value = module.tts.urls
}
