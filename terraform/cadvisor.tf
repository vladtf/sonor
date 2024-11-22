// cAdvisor Service Account
resource "kubernetes_service_account" "cadvisor_sa" {
  metadata {
    name = "cadvisor-sa"
  }
}

// cAdvisor DaemonSet
resource "kubernetes_daemonset" "cadvisor" {
  metadata {
    name = "cadvisor-daemonset"
    labels = {
      app = "cadvisor"
    }
  }

  spec {
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

          volume_mount {
            name       = "rootfs"
            mount_path = "/rootfs"
            read_only  = true
          }

          volume_mount {
            name       = "var-run"
            mount_path = "/var/run"
            read_only  = false
          }

          volume_mount {
            name       = "sys"
            mount_path = "/sys"
            read_only  = true
          }

          volume_mount {
            name       = "docker"
            mount_path = "/var/lib/docker"
            read_only  = true
          }

          volume_mount {
            name       = "cgroup"
            mount_path = "/sys/fs/cgroup"
            read_only  = true
          }
        }

        volume {
          name = "rootfs"
          host_path {
            path = "/"
          }
        }

        volume {
          name = "var-run"
          host_path {
            path = "/var/run"
          }
        }

        volume {
          name = "sys"
          host_path {
            path = "/sys"
          }
        }

        volume {
          name = "docker"
          host_path {
            path = "/var/lib/docker"
          }
        }

        volume {
          name = "cgroup"
          host_path {
            path = "/sys/fs/cgroup"
          }
        }
      }
    }
  }

  depends_on = [
    kubernetes_service_account.cadvisor_sa,
    null_resource.start_minikube
  ]
}

// cAdvisor Service
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
