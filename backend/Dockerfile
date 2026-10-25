# Use official Astral uv image for lightning-fast dependency resolution and installation
FROM ghcr.io/astral-sh/uv:0.13.5 AS uv_bin
FROM python:3.15-slim

WORKDIR /app

# Install system dependencies if required for compilation
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Copy uv binary directly from official image
COPY --from=uv_bin /uv /uvx /bin/

# Copy project configuration and application code
COPY pyproject.toml .
COPY app ./app

# Install project and dependencies via uv
RUN uv pip install --system --no-cache .

# Expose backend port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=10s --timeout=5s --start-period=10s --retries=5 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Start ASGI server via uv
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8080"]
