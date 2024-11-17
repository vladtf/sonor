resource "kubernetes_secret" "db_secret" {
  metadata {
    name = "db-secret"
  }

  data = {
    POSTGRES_DB       = base64encode("mobylab-app")
    POSTGRES_USER     = base64encode("mobylab-app")
    POSTGRES_PASSWORD = base64encode("mobylab-app")
  }
}

# resource "kubernetes_persistent_volume" "postgres_pv" {
#   metadata {
#     name = "postgres-pv"
#   }

#   spec {
#     capacity = {
#       storage = "1Gi"
#     }
#     access_modes = ["ReadWriteOnce"]
#     persistent_volume_reclaim_policy = "Retain"

#     persistent_volume_source {
#       host_path {
#         path = "/mnt/data"
#       }
#     }
#   }
# }

# resource "kubernetes_persistent_volume_claim" "postgres_pvc" {
#   metadata {
#     name = "postgres-pvc"
#   }

#   spec {
#     access_modes = ["ReadWriteOnce"]

#     resources {
#       requests = {
#         storage = "1Gi"
#       }
#     }

#     volume_name = kubernetes_persistent_volume.postgres_pv.metadata[0].name
#   }
# }

resource "kubernetes_deployment" "postgres" {
  metadata {
    name = "db-deployment"
    labels = {
      app = "db"
    }
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "db"
      }
    }

    template {
      metadata {
        labels = {
          app = "db"
        }
      }

      spec {
        image_pull_secrets {
          name = kubernetes_secret.acr_pull_secret.metadata[0].name
        }

        container {
          name  = "postgres"
          image = "${azurerm_container_registry.acr.login_server}/postgres:${var.image_tag}"

          env {
            name = "POSTGRES_DB"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.db_secret.metadata[0].name
                key  = "POSTGRES_DB"
              }
            }
          }

          env {
            name = "POSTGRES_USER"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.db_secret.metadata[0].name
                key  = "POSTGRES_USER"
              }
            }
          }

          env {
            name = "POSTGRES_PASSWORD"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.db_secret.metadata[0].name
                key  = "POSTGRES_PASSWORD"
              }
            }
          }

          port {
            container_port = 5432
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

        #   volume_mount {
        #     mount_path = "/var/lib/postgresql/data"
        #     name       = "postgres-data"
        #   }

          readiness_probe {
            tcp_socket {
              port = 5432
            }
            initial_delay_seconds = 10
            period_seconds        = 5
          }
        }

        # volume {
        #   name = "postgres-data"

        #   persistent_volume_claim {
        #     claim_name = kubernetes_persistent_volume_claim.postgres_pvc.metadata[0].name
        #   }
        # }
      }
    }
  }
}

resource "kubernetes_service" "db_service" {
  metadata {
    name = "db-service"
  }

  spec {
    selector = {
      app = "db"
    }

    port {
      protocol    = "TCP"
      port        = 5432
      target_port = 5432
    }
  }
}
