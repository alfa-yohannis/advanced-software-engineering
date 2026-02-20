module "tts" {
  source = "../modules/tts_stack"

  env          = "dev"
  project_root = abspath("${path.module}/../../..")

  ports = {
    redis         = 6379
    minio_api     = 9000
    minio_console = 9001
    api           = 8000
    web           = 8080
    gateway       = 8088
  }

  mp3_bucket     = var.mp3_bucket
  internal_token = var.internal_token

  # ✅ add these explicitly (match your compose)
  minio_root_user     = "minioadmin"
  minio_root_password = "minioadmin"

  piper_model_config_path = "/models/en_GB-alan-medium.onnx.json"
  piper_length_scale      = "1.0"

  worker_replicas = var.worker_replicas
  mount_models    = var.mount_models
}

variable "mp3_bucket" { type = string }
variable "internal_token" { type = string }
variable "worker_replicas" { 
  type = number 
default = 1 
}

variable "mount_models" { 
    type = bool 
    default = false 
}

output "urls" { 
  value = module.tts.urls 
}
