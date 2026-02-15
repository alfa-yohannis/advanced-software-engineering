terraform {
  required_version = ">= 1.5.0"

  required_providers {
    docker = {
      source  = "kreuzwerker/docker"
      version = "~> 3.0"
    }
  }
}

locals {
  # Paths to application build contexts (absolute, stable)
  api_ctx    = "${var.project_root}/app/services/api"
  web_ctx    = "${var.project_root}/app/services/web"
  worker_ctx = "${var.project_root}/app/services/worker"

  nginx_conf = "${var.project_root}/app/gateway/nginx.conf"
  models_dir = "${var.project_root}/app/models"

  # Internal container ports (match your compose)
  api_internal_port     = 8000
  web_internal_port     = 8000
  gateway_internal_port = 80

  # ✅ Prefix for resource names (unique globally on host)
  prefix = "tts-${var.env}"

  # ✅ Service DNS names stay stable via network aliases
  redis_url   = "redis://redis:6379/0"
  s3_endpoint = "http://minio:9000"
  api_base    = "http://api:${local.api_internal_port}"
}

# Network per environment
resource "docker_network" "net" {
  name = local.prefix
}

# Volume for MinIO data (per env)
resource "docker_volume" "minio_data" {
  name = "minio_data_${var.env}"
}

# Build images (tagged per env so dev/staging/prod can coexist)
resource "docker_image" "api" {
  name = "session05-api:${var.env}"
  build {
    context = local.api_ctx
  }
  keep_locally = true
}

resource "docker_image" "web" {
  name = "session05-web:${var.env}"
  build {
    context = local.web_ctx
  }
  keep_locally = true
}

resource "docker_image" "worker" {
  name = "session05-worker:${var.env}"
  build {
    context = local.worker_ctx
  }
  keep_locally = true
}

# Redis
resource "docker_container" "redis" {
  # ✅ unique container name
  name    = "${local.prefix}-redis"
  image   = "redis:7-alpine"
  restart = "unless-stopped"

  networks_advanced {
    name    = docker_network.net.name
    aliases = ["redis"] # ✅ stable DNS inside this env network
  }

  ports {
    internal = 6379
    external = var.ports.redis
  }
}

# MinIO
resource "docker_container" "minio" {
  # ✅ unique container name
  name    = "${local.prefix}-minio"
  image   = "minio/minio:RELEASE.2025-09-07T16-13-09Z"
  restart = "unless-stopped"

  command = ["server", "/data", "--console-address", ":9001"]

  env = [
    "MINIO_ROOT_USER=${var.minio_root_user}",
    "MINIO_ROOT_PASSWORD=${var.minio_root_password}",
  ]

  networks_advanced {
    name    = docker_network.net.name
    aliases = ["minio"] # ✅ stable DNS
  }

  ports {
    internal = 9000
    external = var.ports.minio_api
  }

  ports {
    internal = 9001
    external = var.ports.minio_console
  }

  volumes {
    volume_name    = docker_volume.minio_data.name
    container_path = "/data"
  }
}

# API
resource "docker_container" "api" {
  # ✅ unique container name
  name    = "${local.prefix}-api"
  image   = docker_image.api.name
  restart = "unless-stopped"

  env = [
    "APP_ENV=${var.env}",
    "REDIS_URL=${local.redis_url}",
    "MP3_BUCKET=${var.mp3_bucket}",
    "PUBLIC_BASE_URL=",
    "INTERNAL_TOKEN=${var.internal_token}",
  ]

  networks_advanced {
    name    = docker_network.net.name
    aliases = ["api"] # ✅ stable DNS
  }

  ports {
    internal = local.api_internal_port
    external = var.ports.api
  }

  depends_on = [
    docker_container.redis,
  ]
}

# WEB
resource "docker_container" "web" {
  # ✅ unique container name
  name    = "${local.prefix}-web"
  image   = docker_image.web.name
  restart = "unless-stopped"

  env = [
    "APP_ENV=${var.env}",
    "S3_ENDPOINT=${local.s3_endpoint}",
    "S3_ACCESS_KEY=${var.minio_root_user}",
    "S3_SECRET_KEY=${var.minio_root_password}",
    "S3_REGION=us-east-1",
    "MP3_BUCKET=${var.mp3_bucket}",
  ]

  networks_advanced {
    name    = docker_network.net.name
    aliases = ["web"] # ✅ stable DNS
  }

  ports {
    internal = local.web_internal_port
    external = var.ports.web
  }

  depends_on = [
    docker_container.minio,
  ]
}

# Gateway (nginx) - mounts nginx.conf from host
resource "docker_container" "gateway" {
  # ✅ unique container name
  name    = "${local.prefix}-gateway"
  image   = "nginx:1.27-alpine"
  restart = "unless-stopped"

  networks_advanced {
    name    = docker_network.net.name
    aliases = ["gateway"] # optional
  }

  ports {
    internal = local.gateway_internal_port
    external = var.ports.gateway
  }

  volumes {
    host_path      = local.nginx_conf
    container_path = "/etc/nginx/conf.d/default.conf"
    read_only      = true
  }

  depends_on = [
    docker_container.api,
    docker_container.web,
  ]
}

# Worker(s)
resource "docker_container" "worker" {
  count   = var.worker_replicas
  # ✅ unique per env + per replica
  name    = "${local.prefix}-worker-${count.index}"
  image   = docker_image.worker.name
  restart = "unless-stopped"

  env = [
    "APP_ENV=${var.env}",
    "REDIS_URL=${local.redis_url}",
    "QUEUE_KEY=jobs:queue:${var.env}",
    "API_BASE_URL=${local.api_base}",
    "INTERNAL_TOKEN=${var.internal_token}",

    "S3_ENDPOINT=${local.s3_endpoint}",
    "S3_ACCESS_KEY=${var.minio_root_user}",
    "S3_SECRET_KEY=${var.minio_root_password}",
    "S3_REGION=us-east-1",
    "MP3_BUCKET=${var.mp3_bucket}",

    "PIPER_MODEL_PATH=${var.piper_model_path}",
    "PIPER_MODEL_CONFIG_PATH=${var.piper_model_config_path}",
    "PIPER_LENGTH_SCALE=${var.piper_length_scale}",
  ]

  networks_advanced {
    name = docker_network.net.name
    # no alias needed; workers don’t have to be addressed by name
  }

  dynamic "volumes" {
    for_each = var.mount_models ? [1] : []
    content {
      host_path      = local.models_dir
      container_path = "/models"
      read_only      = true
    }
  }

  depends_on = [
    docker_container.redis,
    docker_container.minio,
    docker_container.api,
  ]
}
