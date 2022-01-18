package com.tuesd4y.routingdashboard.service

import io.kubernetes.client.openapi.models.*
import org.intellij.lang.annotations.Language

object AutoscalerTemplate {
    const val MAX_REPLICAS = 4
    const val AVERAGE_CPU_UTILISATION = 30

    fun buildRealYaml(identifier: String) =
        yamlString
            .replace(identifierPlaceholder, identifier)

    private const val identifierPlaceholder = "{{identifier}}"

    fun buildAutoscaler(identifier: String) = V2beta2HorizontalPodAutoscaler()
        .apiVersion("autoscaling/v2beta2")
        .kind("HorizontalPodAutoscaler")
        .metadata(
            V1ObjectMeta()
                .name("routing-service-$identifier")
                .namespace("routing-service")
        )
        .spec(
            V2beta2HorizontalPodAutoscalerSpec()
                .maxReplicas(MAX_REPLICAS)
                .metrics(
                    listOf(
                        V2beta2MetricSpec().resource(
                            V2beta2ResourceMetricSource()
                                .name("cpu")
                                .target(
                                    V2beta2MetricTarget()
                                        .averageUtilization(AVERAGE_CPU_UTILISATION)
                                        .type("Utilization")
                                )
                        ).type("Resource")
                    )
                )
                .minReplicas(1)
                .scaleTargetRef(
                    V2beta2CrossVersionObjectReference()
                        .apiVersion("apps/v1")
                        .kind("Deployment")
                        .name("routing-service-$identifier")
                )
        )

    @Language("yaml")
    private const val yamlString = """
apiVersion: autoscaling/v2beta2
kind: HorizontalPodAutoscaler
metadata:
  name: routing-service-{{identifier}}
  namespace: routing-service
spec:
  maxReplicas: $MAX_REPLICAS
  metrics:
    - resource:
        name: cpu
        target:
          averageUtilization: $AVERAGE_CPU_UTILISATION
          type: Utilization
      type: Resource
  minReplicas: 1
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: routing-service-{{identifier}}
"""
}