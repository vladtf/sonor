resource "kubernetes_cluster_role" "metrics_clusterrole" {
  metadata {
    name = "metrics-clusterrole"
  }

  rule {
    api_groups = ["*"]
    resources  = ["*"]
    verbs      = ["get", "list", "watch"]
  }
}

resource "kubernetes_cluster_role_binding" "node_exporter_clusterrolebinding" {
  metadata {
    name = "node-exporter-clusterrolebinding"
  }

  subject {
    kind      = "ServiceAccount"
    name      = kubernetes_service_account.node_exporter_sa.metadata[0].name
    namespace = "default"
  }

  role_ref {
    kind     = "ClusterRole"
    name     = kubernetes_cluster_role.metrics_clusterrole.metadata[0].name
    api_group = "rbac.authorization.k8s.io"
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
    kind     = "ClusterRole"
    name     = kubernetes_cluster_role.metrics_clusterrole.metadata[0].name
    api_group = "rbac.authorization.k8s.io"
  }
}

resource "kubernetes_cluster_role_binding" "cadvisor_clusterrolebinding" {
  metadata {
    name = "cadvisor-clusterrolebinding"
  }

  subject {
    kind      = "ServiceAccount"
    name      = kubernetes_service_account.cadvisor_sa.metadata[0].name
    namespace = "default"
  }

  role_ref {
    kind     = "ClusterRole"
    name     = kubernetes_cluster_role.metrics_clusterrole.metadata[0].name
    api_group = "rbac.authorization.k8s.io"
  }
}
