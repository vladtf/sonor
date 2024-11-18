
resource "kubernetes_config_map" "grafana_dashboard" {
  metadata {
    name = "grafana-dashboard"
  }

  data = {
    "sonor-dashboard.json" = <<JSON
{
  "annotations": { /* ...existing dashboard JSON... */ },
  "editable": true,
  /* ...rest of the dashboard configuration... */
}
JSON
  }
}

resource "kubernetes_config_map" "prometheus_dashboards_config" {
  metadata {
    name = "prometheus-dashboards-config"
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
      path: /var/lib/prometheus/dashboards
      foldersFromFilesStructure: true
YAML
  }
}
