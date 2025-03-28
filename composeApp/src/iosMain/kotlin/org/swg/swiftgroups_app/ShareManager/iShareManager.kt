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
actual fun openMapLocationQuery(query : String) {

    val geocoder = CLGeocoder()

    geocoder.geocodeAddressString(query) { placemarks, error ->
        if (error == null) {
            println(error)
        } else {
            val placemarkList = placemarks as? List<CLPlacemark>
            val location = placemarkList?.first()?.location

            if(location != null) {
                val location_lat_long = getCoordinate(location.coordinate)
                val linkQuery = "?ll=${location_lat_long.first},${location_lat_long.second}"
                val urlString = "http://maps.apple.com/${linkQuery}"
                val NSUrlString = NSURL.URLWithString(urlString)
                if(NSUrlString != null) {
                    UIApplication.sharedApplication.openURL(NSUrlString)
                }
            }
        }

    }
}

@OptIn(ExperimentalForeignApi::class)
fun getCoordinate(coordinateValue: CValue<CLLocationCoordinate2D>): Pair<Double, Double> {
    return coordinateValue.useContents {
        Pair(latitude, longitude)
    }
}