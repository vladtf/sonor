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
        image_pull_secrets {
          name = kubernetes_secret.acr_pull_secret.metadata[0].name
        }

        container {
          name  = "backend"
          image = "${azurerm_container_registry.acr.login_server}/backend-image:${var.image_tag}"

          env {
            name = "SPRING_DATASOURCE_URL"
            value_from {
              config_map_key_ref {
                name = "backend-config"
                key  = "SPRING_DATASOURCE_URL"
              }
            }
          }

          env {
            name = "SPRING_DATASOURCE_USERNAME"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.db_secret.metadata[0].name
                key  = "POSTGRES_USER"
              }
            }
          }

          env {
            name = "SPRING_DATASOURCE_PASSWORD"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.db_secret.metadata[0].name
                key  = "POSTGRES_PASSWORD"
              }
            }
          }

          env {
            name  = "AUTHENTICATION_SERVER_URL"
            value = "http://authentication-service:5000"
          }

        #   env {
        #     name  = "JAVA_TOOL_OPTIONS"
        #     value = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
        #   }

          port {
            container_port = 8090
          }

        #   port {
        #     container_port = 5005
        #   }

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
      port        = 8090
      target_port = 8090
    }

    # port {
    #   protocol    = "TCP"
    #   port        = 5005
    #   target_port = 5005
    # }
  }
}
