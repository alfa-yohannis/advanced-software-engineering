module "tts" {
  source = "../modules/tts_stack"

  env          = "dev"
  k8s_overlay  = "dev"
  project_root = abspath("${path.module}/../../..")

  mp3_bucket     = var.mp3_bucket
  internal_token = var.internal_token

  minio_root_user     = "minioadmin"
  minio_root_password = "minioadmin"

  worker_replicas = var.worker_replicas
}

variable "mp3_bucket" { type = string }
variable "internal_token" { type = string }

variable "worker_replicas" {
  type    = number
  default = 1
}

output "urls" {
  value = module.tts.urls
}
