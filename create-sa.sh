#!/bin/bash

# see https://kubernetes.io/docs/reference/access-authn-authz/rbac/#service-account-permissions

kubectl create serviceaccount -n routing-service routing-dashboard-admin

kubectl create rolebinding admin-sa \
  --clusterrole=cluster-admin \
  --serviceaccount=routing-service:routing-dashboard-admin \
  --namespace=routing-service
