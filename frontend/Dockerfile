# Lightweight Alpine NGINX image
FROM nginx:1.27-alpine

# Copy custom Nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy static frontend web files
COPY . /usr/share/nginx/html

# Expose standard HTTP port
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
