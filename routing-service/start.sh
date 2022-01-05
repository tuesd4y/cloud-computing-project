#!/bin/bash

kubectl create namespace routing-service
kubectl apply -f routing-service.yaml
kubectl describe service routing-service
