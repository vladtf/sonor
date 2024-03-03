# Build stage
FROM node:18 as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . ./
RUN npm run build

# Serve stage
FROM node:18
WORKDIR /app
COPY --from=build /app/build .
RUN npm install -g serve
CMD ["serve", "-s", "."]

# Optional: Expose port (e.g., 3000) if you plan to run the container with port mapping
EXPOSE 3000
