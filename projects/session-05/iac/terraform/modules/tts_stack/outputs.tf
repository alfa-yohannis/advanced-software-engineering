output "urls" {
  value = {
    gateway = "http://localhost:${var.ports.gateway}"
    web     = "http://localhost:${var.ports.web}"
    api     = "http://localhost:${var.ports.api}"
    minio   = "http://localhost:${var.ports.minio_console}"
  }
}

output "env" { value = var.env }
output "network" { value = "tts-${var.env}" }
output "bucket" { value = var.mp3_bucket }
