resource "kubernetes_config_map" "backend_config" {
  metadata {
    name      = "backend-config"
    namespace = "default"
  }

  data = {
    SPRING_DATASOURCE_URL = "jdbc:postgresql://${kubernetes_service.postgres_service.metadata[0].name}:5432/${var.db_name}"
  }
}

// database secrets
resource "kubernetes_secret" "backend_secret" {
  metadata {
    name = "backend-secret"
  }

  data = {
    db_user     = var.db_user
    db_password = var.db_password
  }
}


resource "kubernetes_deployment" "backend" {
  metadata {
    name = "backend-deployment"
    labels = {
      app = "backend"
    }
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "backend"
      }
    }

    template {
      metadata {
        labels = {
          app = "backend"
        }
      }

      spec {
        init_container {
          name  = "wait-for-postgres"
          image = "busybox:1.31.1"
          command = ["sh", "-c", "until nc -z ${kubernetes_service.postgres_service.metadata[0].name} 5432; do echo waiting for postgres; sleep 2; done;"]
        }
        container {
          name  = "backend"
          image = docker_image.backend_image.name

          image_pull_policy = "IfNotPresent"
          
          env {
            name = "SPRING_DATASOURCE_URL"
            value_from {
              config_map_key_ref {
                name = kubernetes_config_map.backend_config.metadata[0].name
                key  = "SPRING_DATASOURCE_URL"
              }
            }
          }

          env {
            name = "SPRING_DATASOURCE_USERNAME"
            value = kubernetes_secret.backend_secret.data["db_user"]
          }

          env {
            name = "SPRING_DATASOURCE_PASSWORD"
            value = kubernetes_secret.backend_secret.data["db_password"]
          }

          env {
            name  = "AUTHENTICATION_SERVER_URL"
            value = "http://authentication-service:5000"
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
      }
    }
  }

  depends_on = [
    kubernetes_config_map.backend_config,
    kubernetes_service.postgres_service
  ]
}

resource "kubernetes_service" "backend_service" {
  metadata {
    name = "backend-service"
  }

  spec {
    selector = {
      app = "backend"
    }

    port {
      protocol    = "TCP"
      name        = "http"
      port        = 8090
      target_port = 8090
    }

    port {
      protocol    = "TCP"
      name        = "debug"
      port        = 5005
      target_port = 5005
    }

    type = "NodePort"
  }
}
