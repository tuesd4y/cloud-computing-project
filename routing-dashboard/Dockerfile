FROM openjdk:11

LABEL \
  maintainer="Chris Stelzmüller <chris@tuesd4y.com>" \
  org.opencontainers.image.title="routing-backend" \
  org.opencontainers.image.description="Dashboard application for using AWS EKS for orchestrating OSRM deployments." \
  org.opencontainers.image.authors="Chris Stelzmüller <chris@tuesd4y.com>" \
  org.opencontainers.image.url="https://github.com/tuesd4y/cloud-computing-project" \
  org.opencontainers.image.vendor="https://tuesd4y.com" \
  org.opencontainers.image.licenses="MIT"

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]