package com.tuesd4y.routingdashboard.service

import io.kubernetes.client.custom.IntOrString
import io.kubernetes.client.openapi.models.V1ObjectMeta
import io.kubernetes.client.openapi.models.V1Service
import io.kubernetes.client.openapi.models.V1ServicePort
import io.kubernetes.client.openapi.models.V1ServiceSpec
import org.intellij.lang.annotations.Language
import java.time.LocalDateTime

object ServiceTemplate {
    fun buildRealYaml(identifier: String, source: String, region: String, mode: String, time: LocalDateTime) =
        yamlString
            .replace(identifierPlaceholder, identifier)
            .replace(sourcePlaceholder, source)
            .replace(regionPlaceholder, region)
            .replace(modePlaceholder, mode)
            .replace(timePlaceholder, time.toString())

    private const val identifierPlaceholder = "{{identifier}}"
    private const val sourcePlaceholder = "{{source}}"
    private const val regionPlaceholder = "{{region}}"
    private const val modePlaceholder = "{{mode}}"
    private const val timePlaceholder = "{{time}}"

    fun buildService(identifier: String, source: String, region: String, mode: String, time: LocalDateTime) =
        V1Service().apiVersion("v1").kind("Service").metadata(
            V1ObjectMeta()
                .name("routing-service-$identifier-entrypoint")
                .namespace("routing-service")
                .labels(mapOf(
                    "app" to "routing-server",
                    "routing-config" to identifier
                ))
                .annotations(mapOf(
                    "source-url" to source,
                    "region" to region,
                    "mode" to mode,
                    "time" to time.toString()
                )))
            .spec(V1ServiceSpec()
                .type("ClusterIP")
                .selector(mapOf("routing-config" to identifier))
                .ports(listOf(
                    V1ServicePort()
                        .port(5000)
                        .targetPort(IntOrString(5000))
                        .protocol("TCP")
                )))

    @Language("yaml")
    private const val yamlString =  """
apiVersion: v1
kind: Service
metadata:
  name: routing-service-{{identifier}}-entrypoint
  namespace: routing-service
  labels:
    app: routing-server
    routing-config: {{identifier}}
  annotations: 
    source-url: "{{source}}"
    region: "{{region}}"
    mode: "{{mode}}"
    time: "{{time}}"
spec:
  type: ClusterIP
  selector:
    routing-config: {{identifier}}
  ports:
  - port: 5000
    protocol: TCP
    targetPort: 5000   
    """
}