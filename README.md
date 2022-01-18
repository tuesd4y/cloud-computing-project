# Cloud Computing Project

This repository contains the project and its related files for the course ***Special Topics Cloud Computing Architectures, Processes and Operations*** (510.211) of the JKU Linz.

## Project Proposal

The outdated proposal for this project can be found here: [PROPOSAL.md](PROPOSAL.md)

## Outline and General Idea

The goal of this project was to create a dashboard for deploying routing engines (e.g. [OSRM](http://project-osrm.org/)) and visualising their covered area.

Based on the dashboard, users are able to view which routing engines currently running, visualise which area they cover and deploy new routing engines based on selecting from a set of possible areas where data is existing.

All deployed routing engines shown on a map, such that a user can easily see which areas are already covered and running. Setting up a new routing engine involves three steps: Downloading the street network data, computing the covered area to display in the dashboard, pre-processing the street network data to become a routable graph and deploying the routing engine itself.

The pre-processed street network data is stored on AWS S3 and Kubernetes (via Amazon EKS) is used to deploy the routing engines.

_disclaimer: this will beusing at triply - the company where both Sebastian and Chris work_

### Description

_We want to futher clarify how the exact steps that happen after a user chooses to deploy a new "area" for routing_

When a user chooses to deploy a new routing-area, the following "deployment pipeline" is triggered:

- the dashboard-backend receives the requested area and a transport mode (either car, bike or foot) from the UI and starts a `routing-preprocess` (Kubernetes) job via the REST API.
- the `routing-preprocess` job...
  - downloads data about the street network in the selected area
  - pre-processes the street network data for the selected transport mode
  - uploads the pre-processed result to an S3 bucket
- the dashboard-backend is notified when the pre-processed results were uploaded and creates a kubernetes `routing-app` service
- the `routing-app` downloads the pre-processed routing data and starts serving the routing API

![deployment procedure](img/procedure.png)
A Horizontal Pod Autoscaler (HPA) for every part is deployed. It has the task to manage the pods and if nessasary or close them.

Each routing-app service can horizontally scale (more pods in one service), such that a higher load can still be handled. Each routing-app service has an annotation that outlines which area it covers and which transport mode it can be used for. These annotations are queried by the dashboard-backend in order to list currently deployed services.

### Deployment Architecture

![deployment architecture](img/architecture.png)
