package com.tuesd4y.routingdashboard.service

import com.tuesd4y.routingdashboard.config.AwsCredentials
import com.tuesd4y.routingdashboard.entity.ProcessingFinishedInformation
import com.tuesd4y.routingdashboard.entity.RoutingService
import com.tuesd4y.routingdashboard.entity.StartProcessingInformation
import io.kubernetes.client.openapi.ApiException
import io.kubernetes.client.openapi.apis.AppsV1Api
import io.kubernetes.client.openapi.apis.AutoscalingV2beta2Api
import io.kubernetes.client.openapi.apis.BatchV1Api
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1Deployment
import io.kubernetes.client.openapi.models.V1Job
import io.kubernetes.client.openapi.models.V1Service
import io.kubernetes.client.openapi.models.V2beta2HorizontalPodAutoscaler
import io.kubernetes.client.util.Yaml
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class KubernetesApiService(
    private val coreV1Api: CoreV1Api,
    private val appsV1Api: AppsV1Api,
    private val autoscalingV2beta2Api: AutoscalingV2beta2Api,
    private val batchV1Api: BatchV1Api,
    private val awsCredentials: AwsCredentials
) {
    companion object {
        const val NAMESPACE = "routing-service"
        const val LABEL_SELECTOR = "app=routing-server"
        const val MODE_KEY = "mode"
        const val REGION_KEY = "region"
        const val SOURCE_KEY = "source-url"
        const val TIME_KEY = "time"

		const val download_server_url = "https://download.geofabrik.de/"
        const val s3bucket = "s3://triply-routing-data"
    }

    fun stopServer(identifier: String) {
        autoscalingV2beta2Api.deleteNamespacedHorizontalPodAutoscaler(
            "routing-service-$identifier",
            NAMESPACE,
            null,
            null,
            null,
            null,
            null,
            null
        )
        coreV1Api.deleteNamespacedService(
            "routing-service-$identifier-entrypoint",
            NAMESPACE,
            null,
            null,
            null,
            null,
            null,
            null
        )
        appsV1Api.deleteNamespacedDeployment(
            "routing-service-$identifier",
            NAMESPACE,
            null,
            null,
            null,
            null,
            null,
            null
        )
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

    fun startProcessing(startProcessingInformation: StartProcessingInformation) {


		val region = startProcessingInformation.region
        val mode = startProcessingInformation.mode
        val pbfUrl = download_server_url + startProcessingInformation.pbfLink
        val jobString = JobTemplate.buildRealYaml(
            "$region-$mode",
            awsCredentials.awsAccessKeyId,
            awsCredentials.awsSecretAccessKey,
            s3bucket,
            mode,
            region,
            pbfUrl
        )
        val job = Yaml.load(jobString) as V1Job

        batchV1Api.createNamespacedJob(NAMESPACE, job, null, null, null)
    }

    fun startServer(processingFinishedInformation: ProcessingFinishedInformation): Triple<V1Deployment, V1Service, V2beta2HorizontalPodAutoscaler> {
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

        val autoscalerString = AutoscalerTemplate.buildRealYaml(label)
        val autoscaler = Yaml.load(autoscalerString) as V2beta2HorizontalPodAutoscaler

        try {
            val deploymentResponse = appsV1Api.createNamespacedDeployment(NAMESPACE, deployment, null, null, null)
            val serviceResponse = coreV1Api.createNamespacedService(NAMESPACE, service, null, null, null)
            val autoscalingResponse =
                autoscalingV2beta2Api.createNamespacedHorizontalPodAutoscaler(NAMESPACE, autoscaler, null, null, null)
            return Triple(deploymentResponse, serviceResponse, autoscalingResponse)
        } catch (ex: ApiException) {
            System.err.println("Received API error response body: ${ex.responseBody}")
            throw ex
        }

    }
}
