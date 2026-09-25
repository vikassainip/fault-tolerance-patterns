# External test service
- This repository contains an external test service that can be used for testing purposes.
- `docker build -f Dockerfile-external-service-test -t external-service-test:latest .`
- `docker run -p 9090:8080 external-service-test:latest`