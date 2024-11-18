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
  provisioner "local-exec" {
    command = "minikube image load ${docker_image.frontend_image.name}"
  }
  depends_on = [docker_image.frontend_image]
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
  provisioner "local-exec" {
    command = "minikube image load ${docker_image.backend_image.name}"
  }
  depends_on = [docker_image.backend_image]
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
  provisioner "local-exec" {
    command = "minikube image load ${docker_image.authentication_image.name}"
  }
  depends_on = [docker_image.authentication_image]
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
  provisioner "local-exec" {
    command = "minikube image load ${docker_image.postgres_image.name}"
  }
  depends_on = [docker_image.postgres_image]
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
  provisioner "local-exec" {
    command = "minikube image load ${docker_image.pgadmin_image.name}"
  }
  depends_on = [docker_image.pgadmin_image]
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
  provisioner "local-exec" {
    command = "minikube image load ${docker_image.portainer_image.name}"
  }
  depends_on = [docker_image.portainer_image]
}

