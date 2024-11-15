# Use the official pgAdmin image as the base
FROM dpage/pgadmin4:8.13

# Set environment variables
ENV PGADMIN_DEFAULT_EMAIL=admin@admin.com
ENV PGADMIN_DEFAULT_PASSWORD=admin
ENV PGADMIN_LISTEN_PORT=80

# Copy the servers.json configuration file
COPY servers.json /pgadmin4/servers.json

# Expose the pgAdmin port
EXPOSE 80
