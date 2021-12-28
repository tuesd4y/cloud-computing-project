Project Proposal

## Outline and general idea

The idea of this project is to create a dashboard for deploying routing engines (e.g. [OSRM](http://project-osrm.org/)) and visualising their covered area.

Based on the dashboard, users should be able to view which routing engines are currently running, visualise which area they cover and deploy new routing engines based on selecting from a set of possible areas where data is existing.

All deployed routing engines should be shown on a map, such that a user can easily see which areas are already covered and running. Setting up a new routing engine involves three steps: Downloading the street network data, computing the covered area to display in the dashboard, pre-processing the street network data to become a routable graph and deploying the routing engine itself.

We plan on storing the pre-processed street network data on AWS S3 and using Kubernetes (via Amazon EKS) to deploy the routing engines.

_disclaimer: this would actually be interesting for using at triply - the company where both Sebastian and Chris work_

### more precise description

_We want to futher clarify how the exact steps that happen after a user chooses to deploy a new "area" for routing_

When a user chooses to deploy a new routing-area, the following "deployment pipeline" is triggered:

- the dashboard-backend receives the requested area and a transport mode (e.g. car) from the UI and starts a `routing-preprocess` (Kubernetes) job via the REST API.
- the `routing-preprocess` job...
  - downloads data about the street network in the selected area
  - pre-processes the street network data for the selected transport mode
  - uploads the pre-processed result to an S3 bucket
- the dashboard-backend is notified when the pre-processed results were uploaded and creates a kubernetes `routing-app` service
- the `routing-app` downloads the pre-processed routing data and starts serving the routing API



Each routing-app service can horizontally scale (more pods in one service), such that a higher load can still be handled. Each routing-app service has an annotation that outlines which area it covers and which transport mode it can be used for. These annotations are queried by the dashboard-backend in order to list currently deployed services.

### Deployment Architecture

![deployment architecture](img/architecture.png)

## Areas of Concern

- Dashboard Application UI
- Setup Script for routing engines
- Docker configuration for running routing engine
- Automatic deployment of routing engines
- API requests to show running routing engines

## Milestones

### Milestone 1

- First draft of UI for dashboard application
- Setup scripts for routing engine finished

### Milestone 2

- Docker files for routing engines done
- Automatic deployment of routing engines

### Milestone 3

- Connection between dashboard and deployment API

## Responsibilities

Sebastian Tanzer

- UI for dashboard application

Tim Seferagic

- Backend for deploying services to Kubernetes cluster

- Backend for registering and storing running routing engines

Christopher Stelzmüller

- Setup scripts for routing engines

- Deployment configuration for routing engines