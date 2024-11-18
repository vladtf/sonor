resource "kubernetes_config_map" "backend_config" {
  metadata {
    name      = "backend-config"
    namespace = "default"
  }

  data = {
    SPRING_DATASOURCE_URL = "jdbc:postgresql://postgres-service.default.svc.cluster.local:5432/mobylab-app"
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
          command = ["sh", "-c", "until nc -z postgres-service 5432; do echo waiting for postgres; sleep 2; done;"]
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
            value_from {
              secret_key_ref {
                name = kubernetes_secret.postgres_secret.metadata[0].name
                key  = "POSTGRES_USER"
              }
            }
          }

          env {
            name = "SPRING_DATASOURCE_PASSWORD"
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
