# Routing-Dashboard

This application provides an API for viewing, and creating deployments of routing-services
on the routing-service AWS EKS cluster.

## Configuration

- Make sure you have a valid kubeconfig (usually at ~/.kube/config) file set up locally or configure access to the 
  kubernetes API somehow else
- set the `AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY` environment variables