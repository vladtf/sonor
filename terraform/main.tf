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

variable "image_tag" {
  description = "Tag to use for the Docker images"
  type        = string
  default     = "latest"
}
