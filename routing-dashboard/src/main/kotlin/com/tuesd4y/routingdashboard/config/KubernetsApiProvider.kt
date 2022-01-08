package com.tuesd4y.routingdashboard.config

import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.util.Config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KubernetesApiProvider {

    @Bean
    fun getKubernetesApi(): CoreV1Api {
        val client = Config.defaultClient()
        io.kubernetes.client.openapi.Configuration.setDefaultApiClient(client)
        return CoreV1Api()
    }
}