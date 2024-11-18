resource "kubernetes_secret" "postgres_secret" {
  metadata {
    name = "postgres-secret"
  }

  data = {
    POSTGRES_DB       = "mobylab-app"
    POSTGRES_USER     = "mobylab-app"
    POSTGRES_PASSWORD = "mobylab-app"
  }
}

resource "kubernetes_persistent_volume" "postgres_pv" {
  metadata {
    name = "postgres-pv"
  }

  spec {
    capacity = {
      storage = "1Gi"
    }
    access_modes = ["ReadWriteOnce"]
    persistent_volume_reclaim_policy = "Retain"

    persistent_volume_source {
      host_path {
        path = "/mnt/data"
      }
    }
  }
}

resource "kubernetes_persistent_volume_claim" "postgres_pvc" {
  metadata {
    name = "postgres-pvc"
  }

  spec {
    access_modes = ["ReadWriteOnce"]

    resources {
      requests = {
        storage = "1Gi"
      }
    }

    volume_name = kubernetes_persistent_volume.postgres_pv.metadata[0].name
  }
}

resource "kubernetes_deployment" "postgres" {
  metadata {
    name = "postgres-deployment"
    labels = {
      app = "postgres"
    }
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "postgres"
      }
    }

    template {
      metadata {
        labels = {
          app = "postgres"
        }
      }

      spec {
        container {
          name  = "postgres"
          image = "postgres-image:latest"

          image_pull_policy = "IfNotPresent"
          
          env {
            name = "POSTGRES_DB"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.postgres_secret.metadata[0].name
                key  = "POSTGRES_DB"
              }
            }
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

          volume_mount {
            mount_path = "/var/lib/postgresql/data"
            name       = "postgres-data"
          }

          readiness_probe {
            tcp_socket {
              port = 5432
            }
            initial_delay_seconds = 10
            period_seconds        = 5
          }
        }

        volume {
          name = "postgres-data"

          persistent_volume_claim {
            claim_name = kubernetes_persistent_volume_claim.postgres_pvc.metadata[0].name
          }
        }
      }
    }
  }

  depends_on = []
}

resource "kubernetes_service" "postgres_service" {
  metadata {
    name = "postgres-service"
  }

  spec {
    selector = {
      app = "postgres"
    }

    port {
      protocol    = "TCP"
      port        = 5432
      target_port = 5432
    }
  }
}
