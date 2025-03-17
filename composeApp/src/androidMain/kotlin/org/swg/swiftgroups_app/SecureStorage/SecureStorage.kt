package org.swg.swiftgroups_app.SecureStorage

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import org.swg.swiftgroups_app.MainActivity
import org.swg.swiftgroups_app.SecureStorage.impl.SecureStorageImpl

fun SecureStorage(
    context: Context,
    fileName: String? = null
): SecureStorage = SecureStorageImpl(context, fileName)

actual fun SecureStorage(): SecureStorage {
    return SecureStorageImpl(MainActivity.appContext, null)
}