package com.tuesd4y.routingdashboard.service

import org.intellij.lang.annotations.Language

object DeploymentTemplate {

    fun buildRealYaml(identifier: String, accessKeyId: String, secretAccessKey: String, s3bucket: String) =
        yamlString
            .replace(identifierPlaceholder, identifier)
            .replace(accessKeyIdPlaceholder, accessKeyId)
            .replace(secretAccessKeyPlaceholder, secretAccessKey)
            .replace(s3bucketPlaceholder, s3bucket)

    private const val identifierPlaceholder = "{{identifier}}"
    private const val accessKeyIdPlaceholder = "{{accessKeyId}}"
    private const val secretAccessKeyPlaceholder = "{{secretAccessKey}}"
    private const val s3bucketPlaceholder = "{{s3bucket}}"

    @Language("yaml")
    private const val yamlString =  """
apiVersion: apps/v1
kind: Deployment
metadata:
  name: routing-service-{{identifier}}
  namespace: routing-service

spec:
  replicas: 1
  selector:
    matchLabels:
      routing-config: {{identifier}}
  template:
    metadata:
      labels:
        app: routing-server
        routing-config: {{identifier}}

    spec:
      containers:
      - name: routing-service-{{identifier}}
        image: tuesd4y/osrm-backend-eks:latest
        resources:
          requests:
            memory: "500Mi"
            cpu: "200m"
        env: 
        - name: OSRM_DATA_LABEL
          value: "{{identifier}}"
        - name: OSRM_MODE
          value: "LOAD"
        - name: OSRM_S3_BUCKET
          value: "{{s3bucket}}"
        - name: OSRM_AWS_ACCESS_KEY_ID
          value: "{{accessKeyId}}"
        - name: OSRM_AWS_SECRET_ACCESS_KEY
          value: "{{secretAccessKey}}"
    """
}