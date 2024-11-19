// kube-state-metrics Deployment
resource "kubernetes_deployment" "kube_state_metrics" {
  metadata {
    name = "kube-state-metrics"
    labels = {
      app = "kube-state-metrics"
    }
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "kube-state-metrics"
      }
    }

    template {
      metadata {
        labels = {
          app = "kube-state-metrics"
        }
      }

      spec {
        service_account_name = kubernetes_service_account.kube_state_metrics_sa.metadata[0].name

        container {
          name  = "kube-state-metrics"
          image = "k8s.gcr.io/kube-state-metrics/kube-state-metrics:v2.11.0"

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
    kubernetes_service_account.kube_state_metrics_sa
  ]
}

resource "kubernetes_service_account" "kube_state_metrics_sa" {
  metadata {
    name = "kube-state-metrics-sa"
  }
}

resource "kubernetes_service" "kube_state_metrics_service" {
  metadata {
    name = "kube-state-metrics"
    labels = {
      app = "kube-state-metrics"
    }
  }

  spec {
    port {
      port        = 8080
      target_port = 8080
    }

    selector = {
      app = "kube-state-metrics"
    }
  }
}

// node-exporter Deployment
resource "kubernetes_service_account" "node_exporter_sa" {
  metadata {
    name = "node-exporter-sa"
  }
}

resource "kubernetes_daemonset" "node_exporter" {
  metadata {
    name = "node-exporter"
    labels = {
      app = "node-exporter"
    }
  }

  spec {
    selector {
      match_labels = {
        app = "node-exporter"
      }
    }

    template {
      metadata {
        labels = {
          app = "node-exporter"
        }
      }

      spec {
        service_account_name = kubernetes_service_account.node_exporter_sa.metadata[0].name

        container {
          name  = "node-exporter"
          image = "prom/node-exporter:latest"

          image_pull_policy = "IfNotPresent"

          port {
            container_port = 9100
            host_port      = 9100
          }

          args = [
            "--path.rootfs=/host",
          ]

          volume_mount {
            mount_path = "/host"
            name       = "host"
            read_only  = true
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

        volume {
          name = "host"

          host_path {
            path = "/"
          }
        }
      }
    }
  }

  depends_on = [
    kubernetes_service_account.node_exporter_sa
  ]
}

resource "kubernetes_service" "node_exporter_service" {
  metadata {
    name = "node-exporter"
    labels = {
      app = "node-exporter"
    }
  }

  spec {
    port {
      port        = 9100
      target_port = 9100
    }

    selector = {
      app = "node-exporter"
    }
  }
}
