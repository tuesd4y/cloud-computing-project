package com.tuesd4y.routingdashboard.service

import org.intellij.lang.annotations.Language

object AutoscalerTemplate {
    const val MAX_REPLICAS = 4
    const val AVERAGE_CPU_UTILISATION = 30

    fun buildRealYaml(identifier: String) =
        yamlString
            .replace(identifierPlaceholder, identifier)

    private const val identifierPlaceholder = "{{identifier}}"

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