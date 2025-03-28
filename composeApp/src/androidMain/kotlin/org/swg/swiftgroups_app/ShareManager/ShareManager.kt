package org.swg.swiftgroups_app.ShareManager

import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import org.swg.swiftgroups_app.MainActivity


actual fun shareLink(text: String, subject : String) {

    val share = Intent.createChooser(Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        putExtra(Intent.EXTRA_TITLE, subject)
        type = "text/plain"
    }, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    MainActivity.appContext.startActivity(share)

}

actual fun openMapLocationQuery(query : String) {
    val gmmIntentUri: Uri = "geo:0,0?q=${query}".toUri()
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    mapIntent.setPackage("com.google.android.apps.maps")
    MainActivity.appContext.startActivity(mapIntent)
}