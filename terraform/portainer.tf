resource "kubernetes_service_account" "portainer_sa" {
  metadata {
    name = "portainer-sa"
  }
}

resource "kubernetes_cluster_role" "portainer_clusterrole" {
  metadata {
    name = "portainer-clusterrole"
  }

  rule {
    api_groups = ["", "apps", "extensions", "batch", "autoscaling", "networking.k8s.io", "storage.k8s.io", "rbac.authorization.k8s.io"]
    resources  = ["*"]
    verbs      = ["get", "list", "watch"]
  }
}

resource "kubernetes_cluster_role_binding" "portainer_clusterrolebinding" {
  metadata {
    name = "portainer-clusterrolebinding"
  }

  subject {
    kind      = "ServiceAccount"
    name      = kubernetes_service_account.portainer_sa.metadata[0].name
    namespace = "default"
  }

  role_ref {
    kind     = "ClusterRole"
    name     = kubernetes_cluster_role.portainer_clusterrole.metadata[0].name
    api_group = "rbac.authorization.k8s.io"
  }
}

resource "kubernetes_deployment" "portainer" {
  metadata {
    name = "portainer-deployment"
    labels = {
      app = "portainer"
    }
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "portainer"
      }
    }

    template {
      metadata {
        labels = {
          app = "portainer"
        }
      }

      spec {
        service_account_name = kubernetes_service_account.portainer_sa.metadata[0].name

        container {
          name  = "portainer"
          image = docker_image.portainer_image.name

          image_pull_policy = "IfNotPresent"

          port {
            container_port = 9000
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
    kubernetes_service_account.portainer_sa,
    kubernetes_cluster_role_binding.portainer_clusterrolebinding
  ]
}

resource "kubernetes_service" "portainer_service" {
  metadata {
    name = "portainer-service"
  }

  spec {
    selector = {
      app = "portainer"
    }

    port {
      protocol    = "TCP"
      port        = 9000
      target_port = 9000
    }
  }
}
