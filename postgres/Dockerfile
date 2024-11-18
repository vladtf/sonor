# Use the official PostgreSQL image as the base
FROM postgres:13

# Set environment variables
ENV POSTGRES_DB=mobylab-app
ENV POSTGRES_USER=mobylab-app
ENV POSTGRES_PASSWORD=mobylab-app

# (Optional) Run custom scripts or commands
# COPY ./init.sql /docker-entrypoint-initdb.d/
# This SQL script will be executed at database initialization

# (Optional) Install additional PostgreSQL extensions
# RUN apt-get update && apt-get install -y postgresql-contrib

# Expose the PostgreSQL port
EXPOSE 5432

# The entrypoint and command are inherited from the base image, so you don't need to specify them
