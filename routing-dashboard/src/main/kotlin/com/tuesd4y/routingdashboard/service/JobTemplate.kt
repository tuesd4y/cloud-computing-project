package com.tuesd4y.routingdashboard.service

import io.kubernetes.client.custom.Quantity
import io.kubernetes.client.openapi.models.*
import io.kubernetes.client.proto.Resource
import io.kubernetes.client.proto.V1
import org.intellij.lang.annotations.Language

object JobTemplate {
    fun buildRealYaml(
        identifier: String, accessKeyId: String, secretAccessKey: String, s3bucket: String,
        mode: String, region: String, pbfUrl: String
    ) = yamlString
        .replace(identifierPlaceholder, identifier)
        .replace(accessKeyIdPlaceholder, accessKeyId)
        .replace(secretAccessKeyPlaceholder, secretAccessKey)
        .replace(s3bucketPlaceholder, s3bucket)
        .replace(modePlaceholder, mode)
        .replace(regionPlaceholder, region)
        .replace(pbfUrlPlaceholder, pbfUrl)

    private const val identifierPlaceholder = "{{identifier}}"
    private const val accessKeyIdPlaceholder = "{{accessKeyId}}"
    private const val secretAccessKeyPlaceholder = "{{secretAccessKey}}"
    private const val s3bucketPlaceholder = "{{s3bucket}}"
    private const val modePlaceholder = "{{mode}}"
    private const val regionPlaceholder = "{{region}}"
    private const val pbfUrlPlaceholder = "{{pbfUrl}}"


    fun buildJob(
        identifier: String, accessKeyId: String, secretAccessKey: String, s3bucket: String,
        mode: String, region: String, pbfUrl: String
    ): V1Job = V1Job()
        .apiVersion("batch/v1")
        .kind("Job")
        .metadata(
            V1ObjectMeta()
                .name("routing-process-$identifier")
                .namespace("routing-service")
        )
        .spec(
            V1JobSpec()
                .template(
                    V1PodTemplateSpec().spec(
                        V1PodSpec()
                            .containers(
                                listOf(
                                    V1Container()
                                        .name("routing-service-$identifier")
                                        .image("tuesd4y/osrm-backend-eks:latest")
                                        .resources(
                                            V1ResourceRequirements().requests(
                                                mapOf(
                                                    "memory" to Quantity("1000m"),
                                                    "cpu" to Quantity("500m"),
                                                )
                                            )
                                        )
                                        .env(
                                            listOf(
                                                V1EnvVar().name("OSRM_PBF_URL").value(pbfUrl),
                                                V1EnvVar().name("OSRM_GRAPH_PROFILE").value(mode),
                                                V1EnvVar().name("OSRM_DATA_LABEL").value("$region-$mode"),
                                                V1EnvVar().name("OSRM_MODE").value("CREATE"),
                                                V1EnvVar().name("OSRM_S3_BUCKET").value(s3bucket),
                                                V1EnvVar().name("OSRM_AWS_ACCESS_KEY_ID").value(accessKeyId),
                                                V1EnvVar().name("OSRM_AWS_SECRET_ACCESS_KEY").value(secretAccessKey),
                                                V1EnvVar().name("OSRM_WEBHOOK_URL").value("http://routing-dashboard:8080/servers/processingFinished"),
                                                V1EnvVar().name("OSRM_EXIT_AFTER_UPLOAD").value("1"),
                                            )
                                        )
                                )
                            ).restartPolicy("Never")
                    )
                )
                .ttlSecondsAfterFinished(10)
                .completions(1)
                .backoffLimit(1)
        )


    @Language("yaml")
    private const val yamlString = """
apiVersion: batch/v1
kind: Job
metadata:
  name: routing-process-{{identifier}}
  namespace: routing-service
spec:
  template:
    spec:
      containers:
      - name: routing-service-{{identifier}}
        image: tuesd4y/osrm-backend-eks:latest
        resources:
          requests:
            memory: "1000Mi"
            cpu: "500m"
        env: 
        - name: OSRM_PBF_URL
          value: "{{pbfUrl}}"
        - name: OSRM_GRAPH_PROFILE
          value: "{{mode}}"
        - name: OSRM_DATA_LABEL
          value: "{{region}}-{{mode}}"
        - name: OSRM_MODE
          value: "CREATE"
        - name: OSRM_S3_BUCKET
          value: "{{s3bucket}}"
        - name: OSRM_AWS_ACCESS_KEY_ID
          value: "{{accessKeyId}}"
        - name: OSRM_AWS_SECRET_ACCESS_KEY
          value: "{{secretAccessKey}}"
        - name: OSRM_WEBHOOK_URL
          value: "http://routing-dashboard/processingFinished"
        - name: OSRM_EXIT_AFTER_UPLOAD
          value: "1"
      restartPolicy: Never
  ttlSecondsAfterFinished: 10
  completions: 1
  backoffLimit: 1
    """
}