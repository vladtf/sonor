provider "null" {}

provider "kubernetes" {
  config_path = "~/.kube/config"
}

resource "null_resource" "skaffold_build_push" {
  provisioner "local-exec" {
    command = "cd .. && skaffold build"
    environment = {
      IMAGE_TAG    = var.image_tag
    }
  }

  triggers = {
    # This ensures the resource is triggered whenever there are changes.
    timestamp = timestamp()
  }

  depends_on = []
}