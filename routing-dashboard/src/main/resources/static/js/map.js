var map, map_modal, layer, layer_modal, countryLayer, activeLayer, activeModalLayer;
var runningServices;
var serverUrl = "http://localhost:8080/servers";

function initMainMap() {
	//init main map
	map = new OpenLayers.Map('map');
	layer = new OpenLayers.Layer.OSM("",
		["https://a.tile.geofabrik.de/549e80f319af070f8ea8d0f149a149c2/${z}/${x}/${y}.png",
			"https://b.tile.geofabrik.de/549e80f319af070f8ea8d0f149a149c2/${z}/${x}/${y}.png",
			"https://c.tile.geofabrik.de/549e80f319af070f8ea8d0f149a149c2/${z}/${x}/${y}.png",
		],
		{attribution: "", tileOptions: {crossOriginKeyword: null}}
	);
	map.addLayer(layer);

	layer = new OpenLayers.Layer.Vector("KML", {
		strategies: [new OpenLayers.Strategy.Fixed()],
		protocol: new OpenLayers.Protocol.HTTP({
			url: "https://download.geofabrik.de/europe.kml",
			format: new OpenLayers.Format.KML({
				maxDepth: 2
			})
		}),
		styleMap: new OpenLayers.StyleMap({
			"default": new OpenLayers.Style({
				graphicName: "cross",
				pointRadius: 6,
				fillOpacity: 0.00,
				fillColor: "#00b27a",
				strokeColor: "#00865c",
				strokeWidth: 1
			})
		})
	});
	map.addLayer(layer);
	map.setCenter(new OpenLayers.LonLat(16.373819, 48.208176).transform("EPSG:4326", "EPSG:900913"), 4);

}

function initModalMap(){
	//init modal map
	map_modal = new OpenLayers.Map('map_modal');
	layer_modal = new OpenLayers.Layer.OSM("",
		["https://a.tile.geofabrik.de/549e80f319af070f8ea8d0f149a149c2/${z}/${x}/${y}.png",
			"https://b.tile.geofabrik.de/549e80f319af070f8ea8d0f149a149c2/${z}/${x}/${y}.png",
			"https://c.tile.geofabrik.de/549e80f319af070f8ea8d0f149a149c2/${z}/${x}/${y}.png",
		],
		{attribution: "", tileOptions: {crossOriginKeyword: null}}
	);
	map_modal.addLayer(layer_modal);

	layer_modal = new OpenLayers.Layer.Vector("KML", {
		strategies: [new OpenLayers.Strategy.Fixed()],
		protocol: new OpenLayers.Protocol.HTTP({
			url: "https://download.geofabrik.de/europe.kml",
			format: new OpenLayers.Format.KML({
				maxDepth: 2
			})
		}),
		styleMap: new OpenLayers.StyleMap({
			"default": new OpenLayers.Style({
				graphicName: "cross",
				pointRadius: 6,
				fillOpacity: 0.00,
				fillColor: "#00b27a",
				strokeColor: "#00865c",
				strokeWidth: 1
			})
		})
	});
	map_modal.addLayer(layer_modal);
	map_modal.setCenter(new OpenLayers.LonLat(16.373819, 48.208176).transform("EPSG:4326", "EPSG:900913"), 4);
}

function loadStaticRegionsContent(){
	/*$.get("https://download.geofabrik.de/europe.html", function (data) {
		$(data).find('#subregions').eq(1).find('td.subregion :first-child').each(function () {

			$('.regions').append("<a " +
					"id='" + $(this).text().toLowerCase() + "-item' href='#' class='list-group-item list-group-item-action' data-mdb-toggle='list' role='tab' " +
					"data='" + $(this).attr('href') + "'>" + $(this).text() + "</a>" +
					"<div id='" + $(this).text() + "' class='list-group'></div>");

			$.get("https://download.geofabrik.de/" + $(this).attr('href'), function (subdata) {

				$(subdata).find('#subregions').eq(1).find('td.subregion :first-child').each(function () {

					var id = $(subdata).find('h2:first-child').text();
					console.log(id.length);
					if(id.length<1){
						return;
					}

					$('#' + id).append("<a " +
							"id='" + $(this).text().toLowerCase() + "-item' href='#' class='list-group-item list-group-item-action' data-mdb-toggle='list' role='tab' " +
							"data='europe/" + $(this).attr('href') + "'>" + $(this).text() + '</a>');
				});
			});
		});
	})*/
}

$(document).ready(function () {

	initMainMap();

	$.ajax
	({
		type: "GET",
		url: serverUrl,
		dataType: 'json',
		headers: {
			"Authorization": "Basic " + "dXNlcjpwYXNzd29yZA=="
		},
		success: function (result) {
			runningServices = result;

			bounds = new OpenLayers.Bounds();

			$(result).each(function () {
				var name = $("#" + this.region.split("-")[0] + "-item").text();

				$(".active-regions").append('<a class="list-group-item list-group-item-action" href="#" identifier="' + this.region + '-' + this.mode + '" data="' + this.shapeUrl + '" data-mdb-toggle="list" role="tab"><i class="fa-active fa '+ this.iconClass +'"></i>  ' + name + '</a>');

				countryLayer =
					new OpenLayers.Layer.Vector("KML", {
						strategies: [new OpenLayers.Strategy.Fixed()],
						protocol: new OpenLayers.Protocol.HTTP({
							url: this.shapeUrl,
							format: new OpenLayers.Format.KML({
								maxDepth: 2
							})
						}),
						styleMap: new OpenLayers.StyleMap({
							"default": new OpenLayers.Style({
								graphicName: "cross",
								pointRadius: 6,
								fillOpacity: 0.50,
								fillColor: "#00b27a",
								strokeColor: "#00865c",
								strokeWidth: 1
							})
						})
					});

				bounds.extend(countryLayer);
				map.addLayer(countryLayer);
			});
		}
	});

	$(document).on("click", ".active-regions a", function () {

		console.log(this);
		if (activeLayer) map.removeLayer(activeLayer);

		activeLayer = new OpenLayers.Layer.Vector("KML", {
			strategies: [new OpenLayers.Strategy.Fixed()],
			protocol: new OpenLayers.Protocol.HTTP({
				url: $(this).attr("data"),
				format: new OpenLayers.Format.KML({
					maxDepth: 2
				})
			}),
			styleMap: new OpenLayers.StyleMap({
				"default": new OpenLayers.Style({
					graphicName: "cross",
					pointRadius: 6,
					fillOpacity: 0.00,
					strokeColor: "#ee3619",
					strokeWidth: 2
				})
			})
		});

		activeLayer.events.register("loadend", this, function () {
			map.zoomToExtent(activeLayer.getDataExtent());
		});

		map.addLayer(activeLayer);
	});

	$(document).on("click", ".regions a", function () {

		console.log(this);
		if (activeModalLayer) map_modal.removeLayer(activeModalLayer);

		activeModalLayer = new OpenLayers.Layer.Vector("KML", {
			strategies: [new OpenLayers.Strategy.Fixed()],
			protocol: new OpenLayers.Protocol.HTTP({
				url: "https://download.geofabrik.de/" + $(this).attr("data").split(".")[0] + ".kml",
				format: new OpenLayers.Format.KML({
					maxDepth: 2
				})
			}),
			styleMap: new OpenLayers.StyleMap({
				"default": new OpenLayers.Style({
					graphicName: "cross",
					pointRadius: 6,
					fillOpacity: 0.00,
					strokeColor: "#ee3619",
					strokeWidth: 2
				})
			})
		});

		activeModalLayer.events.register("loadend", this, function () {
			map_modal.zoomToExtent(activeModalLayer.getDataExtent());
		});

		map_modal.addLayer(activeModalLayer);
	});

	var regionToAdd;
	$(document).on("click", "#list-all-regions a", function () {
		regionToAdd = $(this).attr('data');
	});

	var modeToAdd;
	$(document).on("click", "#list-modes a", function () {
		modeToAdd = $(this).attr('data');
		if(modalMapLoaded == 0){
			initModalMap();
			modalMapLoaded = 1;
		}
	});

	var modalMapLoaded = 0, regionToDelete;
	/*$(".modal-content").on('shown', function(){
		alert("modal");
		if(modalMapLoaded = 0){
			initModalMap();
			modalMapLoaded = 1;
		}
	});*/

	$(document).on("click", "#deleteRegion", function () {
		regionToDelete = $(".active-regions .active").attr('identifier');
		$.ajax({
			url: serverUrl + "/" + regionToDelete + "/",
			type: "DELETE",
			headers: {
				"Authorization": "Basic " + "dXNlcjpwYXNzd29yZA=="
			},
			statusCode: {
				202: function (response) {
					console.log(response);
					new Toast({
						message: 'Processing Started: Region was deleted!',
						type: 'success'
					});
				}
			},
			500: function (response) {
				console.log(response);
				new Toast({
					message: 'Processing Error: Something went wrong. Contact Admin.',
					type: 'danger'
				});
			}
		});
	});

	$(document).on("click", ".add-region", function () {
		$.ajax({
			url: serverUrl + "/startProcessing",
			type: "POST",
			contentType : 'application/json',
			headers: {
				"Authorization": "Basic " + "dXNlcjpwYXNzd29yZA=="
			},
			data: JSON.stringify({"htmlLink":regionToAdd, "mode":modeToAdd}),
			statusCode: {
				202: function (response) {
					console.log(response);
					new Toast({
						message: 'Processing Started: New region will be available within the next 24 hours.',
						type: 'success'
					});
				}
				},
				500: function (response) {
					console.log(response);
					new Toast({
						message: 'Processing Error: Something went wrong. Contact Admin.',
						type: 'danger'
					});
			}
		});
	});
})
