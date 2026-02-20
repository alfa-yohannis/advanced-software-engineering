variable "env" {
  type = string
}

variable "project_root" {
  type = string
}

# External ports (host-side)
variable "ports" {
  type = object({
    redis         = number
    minio_api     = number
    minio_console = number
    api           = number
    web           = number
    gateway       = number
  })
}

variable "mp3_bucket" {
  type = string
}

variable "internal_token" {
  type = string
}

variable "minio_root_user" {
  type    = string
  default = "minioadmin"
}

variable "minio_root_password" {
  type    = string
  default = "minioadmin"
}

variable "piper_model_path" {
  type    = string
  default = "/models/en_GB-alan-medium.onnx"
}

variable "piper_model_config_path" {
  type    = string
  default = "/models/en_GB-alan-medium.onnx.json"
}

variable "piper_length_scale" {
  type    = string
  default = "1.0"
}

variable "worker_replicas" {
  type    = number
  default = 1
}

variable "mount_models" {
  type    = bool
  default = false
}
