package org.swg.swiftgroups_app.ShareManager

import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.popoverPresentationController
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

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

@Serializable
data class GeocodeResponse(
    val address: String,
    val latitude: Double,
    val longitude: Double
)

@OptIn(DelicateCoroutinesApi::class)
actual fun openMapLocationQuery(query: String) {
    val client = HttpClient()
    
    GlobalScope.launch {
        try {
            val encodedQuery = query.replace(" ", "+")
            val url = "https://geocoding-swiftgroups.azurewebsites.net/api/geocodeRequest?address=$encodedQuery"
            
            val response = client.get(url)
            val jsonBody = response.bodyAsText()
            
            val geocodeResponse = Json.decodeFromString<GeocodeResponse>(jsonBody)
            val urlString = "http://maps.apple.com/?ll=${geocodeResponse.latitude},${geocodeResponse.longitude}&q=$encodedQuery"
            val nsUrl = NSURL.URLWithString(urlString)
            
            if (nsUrl != null) {
                UIApplication.sharedApplication.openURL(
                    nsUrl,
                    options = emptyMap<Any?, Any>(),
                    completionHandler = null
                )
            } else {
                println("Failed to create URL from string: $urlString")
            }
        } catch (e: Exception) {
            println("Geocoding error: ${e.message}")
        } finally {
            client.close()
        }
    }
}