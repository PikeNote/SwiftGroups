package org.swg.swiftgroups_app.ShareManager


import platform.Foundation.NSBundle
import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.popoverPresentationController
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonIgnoreUnknownKeys


val viewController = UIApplication.sharedApplication.keyWindow?.rootViewController

actual fun shareLink(text: String, subject: String) {
    val activityController = UIActivityViewController(
        activityItems = listOf(text),
        applicationActivities = null
    )
    if (viewController != null) {
        activityController.popoverPresentationController?.sourceView = viewController.view
        viewController.presentViewController(activityController, animated = true, completion = null)
    }
    activityController.setTitle(subject)
}

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class GoogleGeocodeResponse(
    val results: List<Result>,
    val status: String
)

@Serializable
data class Result(
    val geometry: Geometry
)

@Serializable
data class Geometry(
    val location: Location
)

@Serializable
data class Location(
    val lat: Double,
    val lng: Double
)

@OptIn(DelicateCoroutinesApi::class)
actual fun openMapLocationQuery(query: String) {
    val googleApiKey: String =
        NSBundle.mainBundle.objectForInfoDictionaryKey("GoogleApiKey") as? String ?: ""

    val client = HttpClient()

    GlobalScope.launch {
        try {
            val url = "https://maps.googleapis.com/maps/api/geocode/json?" +
                    "address=${query.replace(" ", "+")}&key=$googleApiKey"

            val response: HttpResponse = client.get(url)
            val jsonBody = response.bodyAsText()

            val geocodeResponse =  Json {
                ignoreUnknownKeys = true
            }.decodeFromString<GoogleGeocodeResponse>(jsonBody)

            if (geocodeResponse.status == "OK" && geocodeResponse.results.isNotEmpty()) {
                val location = geocodeResponse.results.first().geometry.location
                val mapsUrlString = "http://maps.apple.com/?ll=${location.lat},${location.lng}&q=${query.replace(" ", "+")}"
                val nsUrl = NSURL.URLWithString(mapsUrlString)

                if (nsUrl != null) {
                    UIApplication.sharedApplication.openURL(
                        nsUrl,
                        options = emptyMap<Any?, Any?>(),
                        completionHandler = null
                    )                } else {
                    println("Failed to create valid Apple Maps URL.")
                }
            } else {
                println("No location found or invalid response from Google Geocoding.")
            }
        } catch (e: Exception) {
            println("Geocoding error: ${e.message}")
        } finally {
            client.close()
        }
    }
}