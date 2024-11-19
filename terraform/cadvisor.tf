
// cAdvisor Deployment
resource "kubernetes_service_account" "cadvisor_sa" {
  metadata {
    name = "cadvisor-sa"
  }
}

resource "kubernetes_deployment" "cadvisor" {
  metadata {
    name = "cadvisor-deployment"
    labels = {
      app = "cadvisor"
    }
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "cadvisor"
      }
    }

    template {
      metadata {
        labels = {
          app = "cadvisor"
        }
      }

      spec {
        service_account_name = kubernetes_service_account.cadvisor_sa.metadata[0].name

        container {
          name  = "cadvisor"
          image = "gcr.io/cadvisor/cadvisor:latest"

          image_pull_policy = "IfNotPresent"

          port {
            container_port = 8080
          }

          resources {
            limits = {
              memory = "256Mi"
              cpu    = "250m"
            }
            requests = {
              memory = "128Mi"
              cpu    = "125m"
            }
          }
        }
      }
    }
  }

  depends_on = [
    kubernetes_service_account.cadvisor_sa
  ]
}

resource "kubernetes_service" "cadvisor_service" {
  metadata {
    name = "cadvisor-service"
  }

  spec {
    selector = {
      app = "cadvisor"
    }

    port {
      protocol    = "TCP"
      port        = 8080
      target_port = 8080
    }
  }
}
