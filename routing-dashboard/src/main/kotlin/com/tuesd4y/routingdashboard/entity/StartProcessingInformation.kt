package com.tuesd4y.routingdashboard.entity

data class StartProcessingInformation(
	val htmlLink: String,
	val mode: String
){
	val pbfLink : String = htmlLink.split('.')[0] + "-latest.osm.pbf"
	val region : String = htmlLink.substring(htmlLink.lastIndexOf("/")+1, htmlLink.lastIndexOf(".")).replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}


