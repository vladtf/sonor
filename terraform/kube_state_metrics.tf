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
    kubernetes_service_account.kube_state_metrics_sa,
    null_resource.start_minikube
  ]
}

resource "kubernetes_service_account" "kube_state_metrics_sa" {
  metadata {
    name = "kube-state-metrics-sa"
  }
}

resource "kubernetes_cluster_role_binding" "kube_state_metrics_clusterrolebinding" {
  metadata {
    name = "kube-state-metrics-clusterrolebinding"
  }

  subject {
    kind      = "ServiceAccount"
    name      = kubernetes_service_account.kube_state_metrics_sa.metadata[0].name
    namespace = "default"
  }

  role_ref {
    kind      = "ClusterRole"
    name      = "cluster-admin"
    api_group = "rbac.authorization.k8s.io"
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
