resource "kubernetes_config_map" "frontend_config" {
  metadata {
    name      = "frontend-config"
    namespace = "default"
  }

  data = {
    REACT_APP_BACKEND_URL = "localhost"
  }
}

resource "kubernetes_deployment" "frontend" {
  metadata {
    name = "frontend-deployment"
    labels = {
      app = "frontend"
    }
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "frontend"
      }
    }

    template {
      metadata {
        labels = {
          app = "frontend"
        }
      }

      spec {
        container {
          name  = "frontend"
          image = "${azurerm_container_registry.acr.login_server}/frontend-image:${var.image_tag}"

          env_from {
            config_map_ref {
              name = "frontend-config"
            }
          }

          port {
            container_port = 3000
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
    azurerm_role_assignment.aks_acr_pull
  ]
}

resource "kubernetes_service" "frontend_service" {
  metadata {
    name = "frontend-service"
  }

  spec {
    selector = {
      app = "frontend"
    }

    port {
      protocol    = "TCP"
      port        = 3000
      target_port = 3000
    }

    type = "NodePort"
  }
}