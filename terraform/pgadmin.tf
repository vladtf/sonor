
resource "kubernetes_config_map" "pgadmin_servers_config" {
  metadata {
    name = "pgadmin-servers-config"
  }

  data = {
    "servers.json" = <<JSON
{
  "Servers": {
    "1": {
      "Group": "Servers",
      "Name": "My Local Postgres",
      "Host": "postgres-service",
      "Port": 5432,
      "MaintenanceDB": "mobylab-app",
      "Username": "mobylab-app",
      "PassFile": "/pgpass",
      "SSLMode": "prefer"
    }
  }
}
JSON
  }
}

resource "kubernetes_secret" "pgpass_secret" {
  metadata {
    name = "pgpass-secret"
  }

  type = "Opaque"

  data = {
    pgpass = "postgres-service:5432:*:mobylab-app:mobylab-app"
  }
}

resource "kubernetes_service_account" "pgadmin_sa" {
  metadata {
    name = "pgadmin-sa"
  }
}

resource "kubernetes_deployment" "pgadmin" {
  metadata {
    name = "pgadmin-deployment"
    labels = {
      app = "pgadmin"
    }
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "pgadmin"
      }
    }

    template {
      metadata {
        labels = {
          app = "pgadmin"
        }
      }

      spec {
        service_account_name = kubernetes_service_account.pgadmin_sa.metadata[0].name

        container {
          name  = "pgadmin"
          image = docker_image.pgadmin_image.name

          image_pull_policy = "IfNotPresent"

          env {
            name  = "PGADMIN_DEFAULT_EMAIL"
            value = "admin@admin.com"
          }

          env {
            name  = "PGADMIN_DEFAULT_PASSWORD"
            value = "admin"
          }

          env {
            name = "PGADMIN_LISTEN_PORT"
            value = "80"
          }

          volume_mount {
            mount_path = "/pgadmin4/servers.json"
            name       = "servers-json-volume"
            sub_path   = "servers.json"
          }

          volume_mount {
            mount_path = "/pgpass"
            name       = "pgpass-volume"
            sub_path   = "pgpass"
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

          port {
            container_port = 80
          }
        }

        volume {
          name = "servers-json-volume"

          config_map {
            name = kubernetes_config_map.pgadmin_servers_config.metadata[0].name
          }
        }

        volume {
          name = "pgpass-volume"

          secret {
            secret_name = kubernetes_secret.pgpass_secret.metadata[0].name
          }
        }
      }
    }
  }

  depends_on = [
    kubernetes_config_map.pgadmin_servers_config,
    kubernetes_secret.pgpass_secret
  ]
}

resource "kubernetes_service" "pgadmin_service" {
  metadata {
    name = "pgadmin-service"
  }

  spec {
    selector = {
      app = "pgadmin"
    }

    port {
      protocol    = "TCP"
      port        = 5050
      target_port = 80
    }
  }
}
