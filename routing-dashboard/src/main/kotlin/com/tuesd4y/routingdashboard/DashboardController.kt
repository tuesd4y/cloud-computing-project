package com.tuesd4y.routingdashboard

import com.tuesd4y.routingdashboard.entity.ProcessingFinishedInformation
import com.tuesd4y.routingdashboard.entity.RoutingService
import com.tuesd4y.routingdashboard.entity.StartProcessingInformation
import com.tuesd4y.routingdashboard.service.KubernetesApiService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/servers")
class DashboardController(private val routingService: KubernetesApiService) {

    @GetMapping
    fun getAllServers(): List<RoutingService> {
        return routingService.getCurrentlyRunningServices()
    }

    @PostMapping("startProcessing")
    fun startProcessing(@RequestBody startProcessingInformation: StartProcessingInformation): ResponseEntity<Any> {

		return ResponseEntity.accepted().build()
    }

    @PostMapping("processingFinished")
    fun processingFinished(@RequestBody processingFinishedInformation: ProcessingFinishedInformation): ResponseEntity<Any> {
        val (deployment, service) = routingService.startServer(processingFinishedInformation)
        return ResponseEntity.ok(mapOf(
            "deploymentMetadata" to deployment.metadata!!,
            "serviceMetadata" to service.metadata!!
        ))
    }

	//TODO Shutdown / Stop
}
