resource "kubernetes_deployment" "authentication" {
  metadata {
    name = "authentication-deployment"
    labels = {
      app = "authentication"
    }
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "authentication"
      }
    }

    template {
      metadata {
        labels = {
          app = "authentication"
        }
      }

      spec {

        init_container {
          name  = "wait-for-postgres"
          image = "busybox:1.31.1"
          command = ["sh", "-c", "until nc -z ${kubernetes_service.postgres_service.metadata[0].name} 5432; do echo waiting for postgres; sleep 2; done;"]
        }
        container {
          name  = "authentication"
          image = docker_image.authentication_image.name

          image_pull_policy = "IfNotPresent"

          env {
            name  = "FLASK_APP"
            value = "app.py"
          }

          env {
            name  = "FLASK_RUN_HOST"
            value = "0.0.0.0"
          }

          env {
            name  = "SECRET_KEY"
            value = var.authentication_secret
          }

          env {
            name  = "POSTGRES_HOST"
            value = kubernetes_service.postgres_service.metadata[0].name
          }

          env {
            name  = "POSTGRES_DB"
            value = var.db_name
          }


          env {
            name = "POSTGRES_USER"
            value = var.db_user
          }

          env {
            name = "POSTGRES_PASSWORD"
            value = var.db_password
          }

          env {
            name  = "AUTHENTICATION_SERVER_URL"
            value = "http://authentication-service:5000"
          }

          env {
            name  = "JAVA_TOOL_OPTIONS"
            value = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
          }

          port {
            container_port = 8090
          }

          port {
            container_port = 5005
          }

          resources {
            limits = {
              memory = "512Mi"
              cpu    = "500m"
            }
            requests = {
              memory = "256Mi"
              cpu    = "250m"
            }
          }
        }

        // ...existing volumes...
      }
    }
  }

  depends_on = [
    kubernetes_config_map.backend_config,
    kubernetes_service.postgres_service,
    null_resource.start_minikube
  ]
}

resource "kubernetes_service" "authentication_service" {
  metadata {
    name = "authentication-service"
  }

  spec {
    selector = {
      app = "authentication"
    }

    port {
      protocol    = "TCP"
      port        = 5000
      target_port = 5000
    }
  }
}
