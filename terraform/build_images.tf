resource "docker_image" "frontend_image" {
  name = "frontend-image:${var.image_tag}"
  build {
    context    = "${path.module}/../frontend"
    dockerfile = "Dockerfile"
  }
  triggers = {
    always_build = "${timestamp()}"
  }
}

resource "null_resource" "load_frontend_image" {
  triggers = {
    docker_image_id = docker_image.frontend_image.image_id
  }
  provisioner "local-exec" {
    command = "minikube image load ${docker_image.frontend_image.name}"
  }

}

resource "docker_image" "backend_image" {
  name = "backend-image:${var.image_tag}"
  build {
    context    = "${path.module}/../backend"
    dockerfile = "Dockerfile"
  }
  triggers = {
    always_build = "${timestamp()}"
  }
}

resource "null_resource" "load_backend_image" {
  triggers = {
    docker_image_id = docker_image.backend_image.image_id
  }
  provisioner "local-exec" {
    command = "minikube image load ${docker_image.backend_image.name}"
  }

}

resource "docker_image" "authentication_image" {
  name = "authentication-image:${var.image_tag}"
  build {
    context    = "${path.module}/../authentication"
    dockerfile = "Dockerfile"
  }
  triggers = {
    always_build = "${timestamp()}"
  }
}

resource "null_resource" "load_authentication_image" {
  triggers = {
    docker_image_id = docker_image.authentication_image.image_id
  }
  provisioner "local-exec" {
    command = "minikube image load ${docker_image.authentication_image.name}"
  }

}

resource "docker_image" "postgres_image" {
  name = "postgres-image:${var.image_tag}"
  build {
    context    = "${path.module}/../postgres"
    dockerfile = "Dockerfile"
  }
  triggers = {
    always_build = "${timestamp()}"
  }
}

resource "null_resource" "load_postgres_image" {
  triggers = {
    docker_image_id = docker_image.postgres_image.image_id
  }
  provisioner "local-exec" {
    command = "minikube image load ${docker_image.postgres_image.name}"
  }

}

resource "docker_image" "pgadmin_image" {
  name = "pgadmin-image:${var.image_tag}"
  build {
    context    = "${path.module}/../pgadmin"
    dockerfile = "Dockerfile"
  }
  triggers = {
    always_build = "${timestamp()}"
  }
}

resource "null_resource" "load_pgadmin_image" {
  triggers = {
    docker_image_id = docker_image.pgadmin_image.image_id
  }
  provisioner "local-exec" {
    command = "minikube image load ${docker_image.pgadmin_image.name}"
  }

}

resource "docker_image" "portainer_image" {
  name = "portainer-image:${var.image_tag}"
  build {
    context    = "${path.module}/../portainer"
    dockerfile = "Dockerfile"
  }
  triggers = {
    always_build = "${timestamp()}"
  }
}

resource "null_resource" "load_portainer_image" {
  triggers = {
    docker_image_id = docker_image.portainer_image.image_id
  }
  provisioner "local-exec" {
    command = "minikube image load ${docker_image.portainer_image.name}"
  }

}

