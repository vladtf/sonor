
resource "kubernetes_service_account" "authentication_sa" {
  metadata {
    name = "authentication-sa"
  }
}

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
        service_account_name = kubernetes_service_account.authentication_sa.metadata[0].name

        container {
          name  = "authentication"
          image = "authentication-image:latest"

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
            name  = "SQLALCHEMY_DATABASE_URI"
            value = "postgresql://mobylab-app:mobylab-app@postgres-service.default.svc.cluster.local:5432/mobylab-app"
          }

          env {
            name  = "SECRET_KEY"
            value = "your_secret_key"
          }

          env {
            name  = "POSTGRES_HOST"
            value = "postgres-service.default.svc.cluster.local"
          }

          env {
            name  = "POSTGRES_DB"
            value = "mobylab-app"
          }


          env {
            name = "POSTGRES_USER"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.postgres_secret.metadata[0].name
                key  = "POSTGRES_USER"
              }
            }
          }

          env {
            name = "POSTGRES_PASSWORD"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.postgres_secret.metadata[0].name
                key  = "POSTGRES_PASSWORD"
              }
            }
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
    kubernetes_service.postgres_service
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
