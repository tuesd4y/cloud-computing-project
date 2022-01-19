package com.tuesd4y.routingdashboard.entity

data class RoutingService(
	val region: String,
	val mode: String,
	val sourceUrl: String,
	val setupTime: String
){
	//more generic shapePath creation needed
	val shapePath : String = "data/" + sourceUrl.substring(sourceUrl.lastIndexOf("europe"), sourceUrl.lastIndexOf("-")) + ".kml"
	val iconClass : String = when (mode) {
		"car" -> "fa fa-automobile"
		"bike" -> "fa fa-bicycle"
		"bicycle" -> "fa fa-bicycle"
		"foot" -> "fas fa-walking"
		"walk" -> "fas fa-walking"
		else -> "fa fa-question"
	}
}
