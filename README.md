# Sonor - social app

[Repository for the Sonor social app](https://github.com/vladtf/sonor).

## Table of Contents

- [Sonor - social app](#sonor---social-app)
  - [Table of Contents](#table-of-contents)
  - [Run Docker Compose](#run-docker-compose)
  - [Stop Docker Compose](#stop-docker-compose)
  - [Swagger](#swagger)
  - [Kubernetes](#kubernetes)
  - [Skaffold](#skaffold)
  - [Terraform](#terraform)
    - [Prerequisites](#prerequisites)
    - [Start](#start)
  - [Known issues](#known-issues)

## Run Docker Compose

You need docker and docker-compose installed on your machine.

```bash
# check if docker is installed
❯ docker --version

# check if docker-compose is installed
❯ docker-compose --version

# check if docker is running
❯ docker ps

# run docker-compose
❯ docker-compose up --build -d
```
## Stop Docker Compose

```bash
# stop docker-compose
❯ docker-compose down -v
```

## Swagger

Navigate to [http://localhost:8090/swagger-ui](http://localhost:8090/swagger-ui) to see the Swagger documentation.


## Kubernetes

- Start minikube
```bash
❯ minikube start
```

- Deploy the application
```bash
❯ kubectl apply -f kubernetes-deployment.yaml
```

- Check the status of the pods
```bash
❯ kubectl get pods
```

- Get the ip address of the minikube
```bash
❯ minikube ip
```

- Open the application in the browser
```bash
❯ minikube service frontend-servic
```

- Stop deployment
```bash
❯ kubectl delete -f kubernetes-deployment.yaml
```

- Stop minikube
```bash
❯ minikube stop
```

## Skaffold

- Start minikube
```bash
❯ minikube start
```

- Deploy the application
```bash
❯ skaffold dev
```

- Stop deployment
```bash 
❯ skaffold delete
```

- Stop minikube
```bash
❯ minikube stop
```

## Terraform

### Prerequisites

- Create a `terraform.tfvars` file using the `terraform.tfvars.example` as a template.

- Create the minikube cluster using the following command

```bash
❯ minikube start
```

- Install CNI plugin

```bash
❯ minikube addons enable cilium
```

- Add 2 worker nodes to the cluster

```bash
❯ minikube node add --worker
❯ minikube node add --worker
```

- Load the docker images into the minikube cluster

```bash
❯ minikube image load frontend:latest
❯ minikube image load backend:latest
```

- Stop and delete the cluster

```bash
❯ minikube stop
❯ minikube delete
```

### Start

- Initialize terraform
```bash
❯ terraform init
```

- Plan the
```bash
❯ terraform plan
```

- Start the minikube cluster
```bash
❯ terraform apply --target=null_resource.start_minikube
```

> Note: The `--target=null_resource.start_minikube` flag is used to start the minikube cluster only. This is needed because the kubeconfig file is generated only after the cluster is started. 

- Apply the changes
```bash
❯ terraform apply
```

- Forwards the ports
```bash
❯ ./scripts/forward-all.ps1
```


## Known issues

* The port-forwarding has to be done manually for all the services.
