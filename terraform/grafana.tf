
resource "kubernetes_config_map" "grafana_dashboards" {
  metadata {
    name = "grafana-dashboard"
  }

  data = {
    "sonor-dashboard.json" = <<JSON
{
  "annotations": {
    "list": [
      {
        "builtIn": 1,
        "datasource": {
          "type": "grafana",
          "uid": "-- Grafana --"
        },
        "enable": true,
        "hide": true,
        "iconColor": "rgba(0, 211, 255, 1)",
        "name": "Annotations & Alerts",
        "type": "dashboard"
      }
    ]
  },
  "editable": true,
  "fiscalYearStartMonth": 0,
  "graphTooltip": 0,
  "links": [],
  "panels": [
    {
      "datasource": "Prometheus",
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "palette-classic"
          },
          "custom": {
            "axisBorderShow": false,
            "axisCenteredZero": false,
            "axisColorMode": "text",
            "axisLabel": "",
            "axisPlacement": "auto",
            "barAlignment": 0,
            "barWidthFactor": 0.6,
            "drawStyle": "line",
            "fillOpacity": 0,
            "gradientMode": "none",
            "hideFrom": {
              "legend": false,
              "tooltip": false,
              "viz": false
            },
            "insertNulls": false,
            "lineInterpolation": "linear",
            "lineWidth": 1,
            "pointSize": 5,
            "scaleDistribution": {
              "type": "linear"
            },
            "showPoints": "auto",
            "spanNulls": false,
            "stacking": {
              "group": "A",
              "mode": "none"
            },
            "thresholdsStyle": {
              "mode": "off"
            }
          },
          "mappings": [],
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "green",
                "value": null
              },
              {
                "color": "red",
                "value": 80
              }
            ]
          }
        },
        "overrides": []
      },
      "gridPos": {
        "h": 11,
        "w": 12,
        "x": 0,
        "y": 0
      },
      "id": 1,
      "options": {
        "legend": {
          "calcs": [],
          "displayMode": "list",
          "placement": "bottom",
          "showLegend": true
        },
        "tooltip": {
          "mode": "single",
          "sort": "none"
        }
      },
      "pluginVersion": "11.3.0+security-01",
      "targets": [
        {
          "disableTextWrap": false,
          "editorMode": "builder",
          "expr": "sum by(container_label_io_kubernetes_pod_name) (container_memory_usage_bytes{container_label_io_kubernetes_pod_namespace=\"default\"}) / 1000000",
          "fullMetaSearch": false,
          "includeNullMetadata": true,
          "legendFormat": "__auto",
          "range": true,
          "refId": "A",
          "useBackend": false
        }
      ],
      "title": "Memory usage (MB)",
      "type": "timeseries"
    },
    {
      "datasource": "Prometheus",
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "palette-classic"
          },
          "custom": {
            "axisBorderShow": false,
            "axisCenteredZero": false,
            "axisColorMode": "text",
            "axisLabel": "",
            "axisPlacement": "auto",
            "barAlignment": 0,
            "barWidthFactor": 0.6,
            "drawStyle": "line",
            "fillOpacity": 0,
            "gradientMode": "none",
            "hideFrom": {
              "legend": false,
              "tooltip": false,
              "viz": false
            },
            "insertNulls": false,
            "lineInterpolation": "linear",
            "lineWidth": 1,
            "pointSize": 5,
            "scaleDistribution": {
              "type": "linear"
            },
            "showPoints": "auto",
            "spanNulls": false,
            "stacking": {
              "group": "A",
              "mode": "none"
            },
            "thresholdsStyle": {
              "mode": "off"
            }
          },
          "mappings": [],
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "green"
              },
              {
                "color": "red",
                "value": 80
              }
            ]
          }
        },
        "overrides": []
      },
      "gridPos": {
        "h": 11,
        "w": 12,
        "x": 12,
        "y": 0
      },
      "id": 2,
      "options": {
        "legend": {
          "calcs": [],
          "displayMode": "list",
          "placement": "bottom",
          "showLegend": true
        },
        "tooltip": {
          "mode": "single",
          "sort": "none"
        }
      },
      "pluginVersion": "11.3.0+security-01",
      "targets": [
        {
          "datasource": "Prometheus",
          "disableTextWrap": false,
          "editorMode": "builder",
          "expr": "kube_deployment_status_replicas_unavailable",
          "fullMetaSearch": false,
          "includeNullMetadata": true,
          "legendFormat": "{{deployment}}",
          "range": true,
          "refId": "A",
          "useBackend": false
        }
      ],
      "title": "Kube Deployments Unavailable",
      "type": "timeseries"
    },
    {
      "datasource": "Prometheus",
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "palette-classic"
          },
          "custom": {
            "axisBorderShow": false,
            "axisCenteredZero": false,
            "axisColorMode": "text",
            "axisLabel": "",
            "axisPlacement": "auto",
            "barAlignment": 0,
            "barWidthFactor": 0.6,
            "drawStyle": "line",
            "fillOpacity": 0,
            "gradientMode": "none",
            "hideFrom": {
              "legend": false,
              "tooltip": false,
              "viz": false
            },
            "insertNulls": false,
            "lineInterpolation": "linear",
            "lineWidth": 1,
            "pointSize": 5,
            "scaleDistribution": {
              "type": "linear"
            },
            "showPoints": "auto",
            "spanNulls": false,
            "stacking": {
              "group": "A",
              "mode": "none"
            },
            "thresholdsStyle": {
              "mode": "off"
            }
          },
          "mappings": [],
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "green"
              },
              {
                "color": "red",
                "value": 80
              }
            ]
          }
        },
        "overrides": []
      },
      "gridPos": {
        "h": 11,
        "w": 12,
        "x": 0,
        "y": 11
      },
      "id": 3,
      "options": {
        "legend": {
          "calcs": [],
          "displayMode": "list",
          "placement": "bottom",
          "showLegend": true
        },
        "tooltip": {
          "mode": "single",
          "sort": "none"
        }
      },
      "pluginVersion": "11.3.0+security-01",
      "targets": [
        {
          "datasource": "Prometheus",
          "disableTextWrap": false,
          "editorMode": "builder",
          "expr": "rate(api_requests_total[1m])",
          "fullMetaSearch": false,
          "includeNullMetadata": true,
          "legendFormat": "__auto",
          "range": true,
          "refId": "A",
          "useBackend": false
        }
      ],
      "title": "Api Requests",
      "type": "timeseries"
    },
    {
      "datasource": "Prometheus",
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "palette-classic"
          },
          "custom": {
            "axisBorderShow": false,
            "axisCenteredZero": false,
            "axisColorMode": "text",
            "axisLabel": "",
            "axisPlacement": "auto",
            "barAlignment": 0,
            "barWidthFactor": 0.6,
            "drawStyle": "line",
            "fillOpacity": 0,
            "gradientMode": "none",
            "hideFrom": {
              "legend": false,
              "tooltip": false,
              "viz": false
            },
            "insertNulls": false,
            "lineInterpolation": "linear",
            "lineWidth": 1,
            "pointSize": 5,
            "scaleDistribution": {
              "type": "linear"
            },
            "showPoints": "auto",
            "spanNulls": false,
            "stacking": {
              "group": "A",
              "mode": "none"
            },
            "thresholdsStyle": {
              "mode": "off"
            }
          },
          "mappings": [],
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "green",
                "value": null
              },
              {
                "color": "red",
                "value": 80
              }
            ]
          }
        },
        "overrides": []
      },
      "gridPos": {
        "h": 11,
        "w": 12,
        "x": 12,
        "y": 11
      },
      "id": 4,
      "options": {
        "legend": {
          "calcs": [],
          "displayMode": "list",
          "placement": "bottom",
          "showLegend": true
        },
        "tooltip": {
          "mode": "single",
          "sort": "none"
        }
      },
      "pluginVersion": "11.3.0+security-01",
      "targets": [
        {
          "datasource": "Prometheus",
          "disableTextWrap": false,
          "editorMode": "builder",
          "expr": "sum by(container_label_io_kubernetes_pod_name) (rate(container_cpu_usage_seconds_total{container_label_io_kubernetes_pod_namespace=\"default\"}[5m]))",
          "fullMetaSearch": false,
          "includeNullMetadata": false,
          "legendFormat": "{{container_label_io_kubernetes_pod_name}}",
          "range": true,
          "refId": "A",
          "useBackend": false
        }
      ],
      "title": "CPU Usage",
      "type": "timeseries"
    }
  ],
  "preload": false,
  "schemaVersion": 40,
  "tags": [],
  "templating": {
    "list": []
  },
  "time": {
    "from": "now-30m",
    "to": "now"
  },
  "timepicker": {},
  "timezone": "browser",
  "title": "Sonor Dashboard",
  "uid": "ae459roud37y8a",
  "version": 1,
  "weekStart": ""
}
JSON
  }
}

locals {
  grafana_dashboard_hash = sha1(kubernetes_config_map.grafana_dashboards.data["sonor-dashboard.json"])
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
    annotations = {
      "checksum/grafana-dashboards" = local.grafana_dashboard_hash
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
        annotations = {
          "checksum/grafana-dashboards" = local.grafana_dashboard_hash
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
            name  = "GF_SERVER_HTTP_PORT"
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
            name = kubernetes_config_map.grafana_dashboards.metadata[0].name
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
    kubernetes_config_map.grafana_dashboards_config,
    kubernetes_config_map.grafana_dashboards
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
