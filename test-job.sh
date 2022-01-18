#!/bin/bash

kubectl apply -f routing-service/routing-job.yaml
kubectl describe jobs -n routing-service
# look for created pod message and copy id

kubectl logs -n routing-service routing-process-maldives-car-POD_ID
