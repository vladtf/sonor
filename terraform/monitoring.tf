// Prometheus ConfigMap
resource "kubernetes_config_map" "prometheus_config" {
  metadata {
    name = "prometheus-config"
  }

  data = {
    "prometheus.yml" = <<YAML
global:
  scrape_interval: 15s
scrape_configs:
  - job_name: 'kubernetes-pods'
    kubernetes_sd_configs:
      - role: pod
    relabel_configs:
      - source_labels: [__meta_kubernetes_pod_label_app]
        action: keep
        regex: .+
  - job_name: 'kube-state-metrics'
    static_configs:
      - targets: ['kube-state-metrics:8080']
  - job_name: 'node-exporter'
    static_configs:
      - targets: ['node-exporter:9100']
  - job_name: 'cadvisor'
    static_configs:
      - targets: ['cadvisor-service:8080']
    metrics_path: /metrics
    scheme: http
YAML
  }
}

// Prometheus Deployment
resource "kubernetes_deployment" "prometheus" {
  metadata {
    name = "prometheus-deployment"
    labels = {
      app = "prometheus"
    }
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "prometheus"
      }
    }

    template {
      metadata {
        labels = {
          app = "prometheus"
        }
      }

      spec {
        container {
          name  = "prometheus"
          image = "prom/prometheus:latest"

          args = [
            "--config.file=/etc/prometheus/prometheus.yml",
          ]

          port {
            container_port = 9090
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
            mount_path = "/etc/prometheus/"
            name       = "prometheus-config-volume"
          }
        }

        volume {
          name = "prometheus-config-volume"

          config_map {
            name = kubernetes_config_map.prometheus_config.metadata[0].name
          }
        }
      }
    }
  }

  depends_on = [
    kubernetes_config_map.prometheus_config
  ]
}

// Prometheus Service
resource "kubernetes_service" "prometheus_service" {
  metadata {
    name = "prometheus-service"
  }

  spec {
    selector = {
      app = "prometheus"
    }

    port {
      protocol    = "TCP"
      port        = 9090
      target_port = 9090
    }
  }
}

// Grafana Datasources ConfigMap
resource "kubernetes_config_map" "grafana_datasources" {
  metadata {
    name = "grafana-datasources"
  }

  data = {
    "datasource.yaml" = <<YAML
apiVersion: 1
datasources:
- name: Prometheus
  type: prometheus
  access: proxy
  url: http://prometheus-service:9090
  isDefault: true
YAML
  }
}

// Grafana Dashboards ConfigMap
resource "kubernetes_config_map" "grafana_dashboards_config" {
  metadata {
    name = "grafana-dashboards-config"
  }

  data = {
    "dashboards.yml" = <<YAML
apiVersion: 1
providers:
  - name: 'default'
    orgId: 1
    folder: ''
    folderUid: ''
    type: file
    disableDeletion: false
    updateIntervalSeconds: 10
    allowUiUpdates: false
    options:
      path: /var/lib/grafana/dashboards
      foldersFromFilesStructure: true
YAML
  }
}

// Grafana Deployment
resource "kubernetes_deployment" "grafana" {
  metadata {
    name = "grafana-deployment"
    labels = {
      app = "grafana"
    }
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "grafana"
      }
    }

    template {
      metadata {
        labels = {
          app = "grafana"
        }
      }

      spec {
        container {
          name  = "grafana"
          image = "grafana/grafana:latest"

          image_pull_policy = "IfNotPresent"

          env {
            name  = "GF_SECURITY_ADMIN_PASSWORD"
            value = "admin"
          }

          env {
            name = "GF_SERVER_HTTP_PORT"
            value = "3100"
          }

          port {
            container_port = 3100
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
            mount_path = "/var/lib/grafana"
            name       = "grafana-storage"
          }

          volume_mount {
            mount_path = "/etc/grafana/provisioning/datasources/datasource.yaml"
            name       = "grafana-datasources"
            sub_path   = "datasource.yaml"
          }

          volume_mount {
            mount_path = "/var/lib/grafana/dashboards/sonor-dashboard.json"
            name       = "grafana-dashboards"
            sub_path   = "sonor-dashboard.json"
          }

          volume_mount {
            mount_path = "/etc/grafana/provisioning/dashboards/dashboards.yml"
            name       = "grafana-dashboards-config"
            sub_path   = "dashboards.yml"
          }
        }

        volume {
          name = "grafana-storage"

          empty_dir {}
        }

        volume {
          name = "grafana-datasources"

          config_map {
            name = kubernetes_config_map.grafana_datasources.metadata[0].name
          }
        }

        volume {
          name = "grafana-dashboards"

          config_map {
            name = kubernetes_config_map.grafana_dashboard.metadata[0].name
          }
        }

        volume {
          name = "grafana-dashboards-config"

          config_map {
            name = kubernetes_config_map.grafana_dashboards_config.metadata[0].name
          }
        }
      }
    }
  }

  depends_on = [
    kubernetes_config_map.grafana_datasources,
    kubernetes_config_map.grafana_dashboards_config
  ]
}

// Grafana Service
resource "kubernetes_service" "grafana_service" {
  metadata {
    name = "grafana-service"
  }

  spec {
    selector = {
      app = "grafana"
    }

    port {
      protocol    = "TCP"
      port        = 3100
      target_port = 3100
    }
  }
}

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
