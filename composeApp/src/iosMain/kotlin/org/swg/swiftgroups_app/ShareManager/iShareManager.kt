package org.swg.swiftgroups_app.ShareManager

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLGeocoder
import platform.CoreLocation.CLLocationCoordinate2D
import platform.CoreLocation.CLPlacemark
import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.popoverPresentationController
import platform.Foundation.NSLocale

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

@OptIn(ExperimentalForeignApi::class)
actual fun openMapLocationQuery(query: String) {
    val geocoder = CLGeocoder()
    val locale = NSLocale(localeIdentifier = "en_US")

    geocoder.geocodeAddressString(query, null, locale) { placemarks, error ->
        if (error != null) {
            println("Geocoding error: $error")
            return@geocodeAddressString
        }

        val placemarkList = placemarks as? List<CLPlacemark>
        val location = placemarkList?.firstOrNull()?.location

        if (location != null) {
            val locationLatLong = getCoordinate(location.coordinate)
            val latitude = locationLatLong.first
            val longitude = locationLatLong.second
            val urlString = "http://maps.apple.com/?ll=$latitude,$longitude&q=${query.replace(" ", "+")}"
            val nsUrl = NSURL.URLWithString(urlString)
            if (nsUrl != null) {
                UIApplication.sharedApplication.openURL(nsUrl, options = emptyMap<Any?, Any>(), completionHandler = null)
            } else {
                println("Failed to create URL from string: $urlString")
            }
        } else {
            println("No location found for the given address: $query")
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
fun getCoordinate(coordinateValue: CValue<CLLocationCoordinate2D>): Pair<Double, Double> {
    return coordinateValue.useContents {
        Pair(latitude, longitude)
    }
}