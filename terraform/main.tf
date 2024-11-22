provider "null" {}

provider "kubernetes" {
  config_path    = "~/.kube/config"
  config_context = "minikube"
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

resource "null_resource" "start_minikube" {
  provisioner "local-exec" {
    command = "minikube start --nodes=3"
  }

  provisioner "local-exec" {
    when    = destroy
    command = "minikube stop"
  }

}
