module "tts" {
  source = "../modules/tts_stack"

  env          = "production"
  k8s_overlay  = "prod"
  project_root = abspath("${path.module}/../../..")

  mp3_bucket     = var.mp3_bucket
  internal_token = var.internal_token

  minio_root_user     = var.minio_root_user
  minio_root_password = var.minio_root_password

  worker_replicas = var.worker_replicas
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
