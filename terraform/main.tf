provider "null" {}

provider "kubernetes" {
  config_path = "~/.kube/config"
}

terraform {
  required_providers {
    docker = {
      source  = "kreuzwerker/docker"
      version = "~> 3.0"
    }
  }
}

provider "docker" {}

// Add Docker resources for each image
resource "docker_image" "frontend_image" {
  name         = "frontend-image"
  build {
    context    = "${path.module}/../frontend"
    dockerfile = "Dockerfile"
  }
}

resource "docker_image" "backend_image" {
  name         = "backend-image"
  build {
    context    = "${path.module}/../backend"
    dockerfile = "Dockerfile"
  }
}

resource "docker_image" "authentication_image" {
  name         = "authentication-image"
  build {
    context    = "${path.module}/../authentication"
    dockerfile = "Dockerfile"
  }
}

resource "docker_image" "postgres_image" {
  name         = "postgres-image"
  build {
    context    = "${path.module}/../postgres"
    dockerfile = "Dockerfile"
  }
}

resource "docker_image" "pgadmin_image" {
  name         = "pgadmin-image"
  build {
    context    = "${path.module}/../pgadmin"
    dockerfile = "Dockerfile"
  }
}

resource "docker_image" "portainer_image" {
  name         = "portainer-image"
  build {
    context    = "${path.module}/../portainer"
    dockerfile = "Dockerfile"
  }
}

