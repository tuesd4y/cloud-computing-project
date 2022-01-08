package com.tuesd4y.routingdashboard.service

import com.tuesd4y.routingdashboard.entity.ProcessingFinishedInformation
import com.tuesd4y.routingdashboard.entity.RoutingService
import io.kubernetes.client.openapi.apis.CoreV1Api
import org.springframework.stereotype.Service

@Service
class KubernetesApiService(private val kubernetesApi: CoreV1Api) {
    companion object {
        const val NAMESPACE =  "routing-service"
        const val LABEL_SELECTOR = "app=routing-server"
        const val MODE_KEY = "mode"
        const val REGION_KEY = "region"
        const val SOURCE_KEY = "source-url"
        const val TIME_KEY = "time"
    }

    fun getCurrentlyRunningServices(): List<RoutingService> {
        val services = kubernetesApi.listNamespacedService(NAMESPACE, null, null, null, null,
            LABEL_SELECTOR, null, null, null, null, false)
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

    fun startServer(processingFinishedInformation: ProcessingFinishedInformation) {
        TODO()
    }
}