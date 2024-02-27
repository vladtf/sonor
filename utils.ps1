# do the migration
dotnet ef migrations add "InitialCreate" --context WebAppDatabaseContext --project .\MobyLabWebProgramming.Infrastructure --startup-project .\MobyLabWebProgramming.Backend

# start compose
docker-compose -f .\docker-compose.yml up -d
