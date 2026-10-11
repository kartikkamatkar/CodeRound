# Multi-stage Dockerfile for CodeRound React Frontend

# Stage 1: Build the static bundle
FROM node:20-alpine AS builder

WORKDIR /app

# Copy package descriptors
COPY package*.json ./

RUN npm ci

# Copy frontend source code
COPY . .

# Build production bundle with Vite
RUN npm run build

# Stage 2: Serve static files with Nginx
FROM nginx:alpine

# Remove default nginx static assets
RUN rm -rf /usr/share/nginx/html/*

# Copy build output to nginx html directory
COPY --from=builder /app/dist /usr/share/nginx/html

# Expose HTTP port
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
