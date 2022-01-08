#!/bin/bash

kubectl create namespace routing-service
kubectl apply -f routing-service.yaml
kubectl describe service routing-service -n routing-service

kubectl port-forward service/routing-service-entrypoint -n routing-service 5001:5000
