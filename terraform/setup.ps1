# get subscriptions
$subs = az account list --query "[].{name:name, id:id}" --output json | ConvertFrom-Json

# get the subscription ID for "Visual Studio Enterprise Subscription"
$subId = ($subs | Where-Object { $_.name -eq "Visual Studio Enterprise Subscription" }).id

# output the subscription ID
Write-Output "Subscription ID: $subId"

# create sp for terraform
az ad sp create-for-rbac --name "terraform-sp" --role="Contributor" --scopes="/subscriptions/$subId" --output json | ConvertFrom-Json

