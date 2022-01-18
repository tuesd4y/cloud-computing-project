package com.tuesd4y.routingdashboard.entity

data class RoutingService(
	val region: String,
	val mode: String,
	val sourceUrl: String,
	val setupTime: String
){
	val shapeUrl : String = sourceUrl.substring(0, sourceUrl.lastIndexOf("-")) + ".kml"
	val iconClass : String = when (mode) {
		"car" -> "fa-automobile"
		"bike" -> "fa-bicycle"
		"foot" -> "fa-walking"
		else -> ""
	}
}
