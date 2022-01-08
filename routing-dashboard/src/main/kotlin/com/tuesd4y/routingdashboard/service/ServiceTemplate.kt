package com.tuesd4y.routingdashboard.service

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