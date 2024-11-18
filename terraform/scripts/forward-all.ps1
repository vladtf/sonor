# Get all services in the cluster with their ports
$services = kubectl get svc --no-headers -o custom-columns="NAME:.metadata.name,PORTS:.spec.ports[*].port"

# Loop through each service and set up port-forwarding
foreach ($service in $services) {
    # Split the service line into name and ports
    $serviceParts = $service -split '\s{2,}'  # Split by two or more spaces
    $svcName = $serviceParts[0]
    $svcPorts = $serviceParts[1] -split ','   # Split ports if multiple

    foreach ($port in $svcPorts) {
        Write-Host "Forwarding ${svcName}:${port} -> localhost:$port"
        # Start port-forwarding as a background job
        Start-Job -ScriptBlock {
            kubectl port-forward "svc/$using:svcName" "$using:port:$using:port"
        }
    }
}

Write-Host "All services are being forwarded. Use 'Get-Job' to monitor jobs. Use 'Get-Job | Stop-Job' to stop all jobs."
