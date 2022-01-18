#!/bin/sh

VERSION="1.1.2"
rm -rf build/libs/*.jar
./gradlew bootJar

docker build --build-arg JAR_FILE=build/libs/\*.jar -t tuesd4y/routing-backend:v${VERSION} -t tuesd4y/routing-backend:latest .

docker push tuesd4y/routing-backend:v${VERSION}
docker push tuesd4y/routing-backend:latest