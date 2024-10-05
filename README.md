# Web Programming - Project

## Table of Contents

- [Web Programming - Project](#web-programming---project)
  - [Table of Contents](#table-of-contents)
  - [Run Docker Compose](#run-docker-compose)
  - [Stop Docker Compose](#stop-docker-compose)
  - [Swagger](#swagger)

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
