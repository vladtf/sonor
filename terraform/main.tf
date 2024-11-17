provider "null" {}

provider "kubernetes" {
  config_path = "~/.kube/config"
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