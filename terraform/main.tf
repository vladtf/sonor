provider "azurerm" {
  features {}

  client_id       = var.client_id
  client_secret   = var.client_secret
  subscription_id = var.subscription_id
  tenant_id       = var.tenant_id
}

provider "null" {}

provider "kubernetes" {
  host                   = azurerm_kubernetes_cluster.aks.kube_config.0.host
  client_certificate     = base64decode(azurerm_kubernetes_cluster.aks.kube_config.0.client_certificate)
  client_key             = base64decode(azurerm_kubernetes_cluster.aks.kube_config.0.client_key)
  cluster_ca_certificate = base64decode(azurerm_kubernetes_cluster.aks.kube_config.0.cluster_ca_certificate)
}

resource "azurerm_resource_group" "rg" {
  name     = "k8s-resource-group"
  location = "East US"
}

resource "azurerm_kubernetes_cluster" "aks" {
  name                = "k8s-cluster"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  dns_prefix          = "k8s"

  default_node_pool {
    name       = "default"
    node_count = 3
    vm_size    = "Standard_DS2_v2"
  }

  identity {
    type = "SystemAssigned"
  }

  network_profile {
    network_plugin    = "azure"
    load_balancer_sku = "standard"
  }

  tags = {
    Environment = "Dev"
  }
}

output "kube_config" {
  value     = azurerm_kubernetes_cluster.aks.kube_config_raw
  sensitive = true
}

output "acr_admin_username" {
  value     = azurerm_container_registry.acr.admin_username
  sensitive = true
}

output "acr_admin_password" {
  value     = azurerm_container_registry.acr.admin_password
  sensitive = true
}

resource "azurerm_container_registry" "acr" {
  name                = "sonork8sregistry"
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
  sku                 = "Basic"
  admin_enabled       = true
}

resource "null_resource" "skaffold_build_push" {
  provisioner "local-exec" {
    command = "az acr login --name ${azurerm_container_registry.acr.name} && cd .. && skaffold build --default-repo=${azurerm_container_registry.acr.login_server}"
    environment = {
      ACR_USERNAME = azurerm_container_registry.acr.admin_username
      ACR_PASSWORD = azurerm_container_registry.acr.admin_password
      IMAGE_TAG    = var.image_tag
    }
  }

  triggers = {
    # This ensures the resource is triggered whenever there are changes.
    timestamp = timestamp()
  }

  depends_on = [azurerm_container_registry.acr]
}