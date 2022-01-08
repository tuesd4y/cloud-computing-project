package com.tuesd4y.routingdashboard.service

import com.tuesd4y.routingdashboard.config.AwsCredentials
import com.tuesd4y.routingdashboard.entity.ProcessingFinishedInformation
import com.tuesd4y.routingdashboard.entity.RoutingService
import io.kubernetes.client.openapi.ApiException
import io.kubernetes.client.openapi.apis.AppsV1Api
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1Deployment
import io.kubernetes.client.openapi.models.V1Service
import io.kubernetes.client.util.Yaml
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class KubernetesApiService(
    private val coreV1Api: CoreV1Api,
    private val appsV1Api: AppsV1Api,
    private val awsCredentials: AwsCredentials
) {
    companion object {
        const val NAMESPACE = "routing-service"
        const val LABEL_SELECTOR = "app=routing-server"
        const val MODE_KEY = "mode"
        const val REGION_KEY = "region"
        const val SOURCE_KEY = "source-url"
        const val TIME_KEY = "time"

        const val s3bucket = "s3://triply-routing-data"
    }

    fun getCurrentlyRunningServices(): List<RoutingService> {
        val services = coreV1Api.listNamespacedService(
            NAMESPACE, null, null, null, null,
            LABEL_SELECTOR, null, null, null, null, false
        )
        return services.items.mapNotNull { apiService ->
            try {
                RoutingService(
                    apiService.metadata!!.annotations!![REGION_KEY]!!,
                    apiService.metadata!!.annotations!![MODE_KEY]!!,
                    apiService.metadata!!.annotations!![SOURCE_KEY]!!,
                    apiService.metadata!!.annotations!![TIME_KEY]!!
                )
            } catch (npe: NullPointerException) {
                // this catch works to filter out all services where the annotations can't be parsed
                null
            }
        }
    }

    fun startServer(processingFinishedInformation: ProcessingFinishedInformation): Pair<V1Deployment, V1Service> {
        val label = processingFinishedInformation.label
        val region = label.replace("-${processingFinishedInformation.mode}", "")
        val deploymentString = DeploymentTemplate.buildRealYaml(
            label,
            awsCredentials.awsAccessKeyId,
            awsCredentials.awsSecretAccessKey,
            s3bucket
        )
        val deployment = Yaml.load(deploymentString) as V1Deployment

        val serviceString = ServiceTemplate.buildRealYaml(
            label,
            processingFinishedInformation.source,
            region,
            processingFinishedInformation.mode,
            LocalDateTime.now()
        )
        val service = Yaml.load(serviceString) as V1Service

        try {
            val deploymentResponse = appsV1Api.createNamespacedDeployment(NAMESPACE, deployment, null, null, null)
            val serviceResponse = coreV1Api.createNamespacedService(NAMESPACE, service, null, null, null)
            return deploymentResponse to serviceResponse
        } catch (ex: ApiException) {

            System.err.println("Received API error response body: ${ex.responseBody}")
            throw ex
        }

    }
}