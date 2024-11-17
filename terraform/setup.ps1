# get subscriptions
$subs = az account list --query "[].{name:name, id:id}" --output json | ConvertFrom-Json

# get the subscription ID for "Visual Studio Enterprise Subscription"
$subId = ($subs | Where-Object { $_.name -eq "Visual Studio Enterprise Subscription" }).id

# output the subscription ID
Write-Output "Subscription ID: $subId"

# create sp for terraform
az ad sp create-for-rbac --name "terraform-sp" --role="Contributor" --scopes="/subscriptions/$subId" --output json | ConvertFrom-Json

# get the sp id
$spId = az ad sp list --display-name "terraform-sp" --query "[].{id:appId}" --output json | ConvertFrom-Json

# allow the sp to assign roles
az role assignment create --role "User Access Administrator" --assignee $spId.id --scope "/subscriptions/$subId/resourceGroups/k8s-resource-group/providers/Microsoft.ContainerRegistry/registries/sonork8sregistry" --output json | ConvertFrom-Json

$aksClusterName = az aks list --resource-group k8s-resource-group --query "[].{name:name}" --output json | ConvertFrom-Json
$aksClusterName = $aksClusterName[0].name

$resourceGroup = az aks list --resource-group k8s-resource-group --query "[].{resourceGroup:resourceGroup}" --output json | ConvertFrom-Json
$resourceGroup = $resourceGroup[0].resourceGroup 

$acr = az acr list --resource-group k8s-resource-group --query "[].{name:name}" --output json | ConvertFrom-Json
$acr = $acr[0].name

az aks update -n $aksClusterName -g $resourceGroup --attach-acr $acr

