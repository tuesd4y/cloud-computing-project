package com.tuesd4y.routingdashboard.config

import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.openapi.apis.AppsV1Api
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.util.Config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KubernetesApiProvider(private val client: ApiClient = Config.defaultClient()) {

    @Bean
    fun getCoreV1api(): CoreV1Api {
        return CoreV1Api(client)
    }

    @Bean
    fun getAppsV1api(): AppsV1Api {
        return AppsV1Api(client)
    }
}