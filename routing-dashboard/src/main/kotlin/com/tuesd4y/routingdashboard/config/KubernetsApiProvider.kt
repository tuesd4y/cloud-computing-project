package com.tuesd4y.routingdashboard.config

import io.kubernetes.client.openapi.Configuration.setDefaultApiClient
import io.kubernetes.client.openapi.apis.AppsV1Api
import io.kubernetes.client.openapi.apis.AutoscalingV2beta2Api
import io.kubernetes.client.openapi.apis.BatchV1Api
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.util.ClientBuilder
import io.kubernetes.client.util.KubeConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.FileReader


@Configuration
class KubernetesApiProvider {

    init {
        val client = ClientBuilder.defaultClient()
        setDefaultApiClient(client)
    }
    @Bean
    fun getCoreV1api(): CoreV1Api {
        return CoreV1Api()
    }

    @Bean
    fun getAppsV1api(): AppsV1Api {
        return AppsV1Api()
    }

    @Bean
    fun getBatchV1api(): BatchV1Api {
        return BatchV1Api()
    }

    @Bean
    fun getAutoScalingV2beta2api(): AutoscalingV2beta2Api {
        return AutoscalingV2beta2Api()
    }
}