package org.swg.swiftgroups_app.ShareManager

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
