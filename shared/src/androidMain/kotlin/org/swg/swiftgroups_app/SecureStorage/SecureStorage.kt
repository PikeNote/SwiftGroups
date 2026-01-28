package org.swg.swiftgroups_app.SecureStorage

import android.content.Context
import org.swg.swiftgroups_app.AndroidApp
import org.swg.swiftgroups_app.SecureStorage.impl.SecureStorageImpl

fun SecureStorage(
    context: Context,
    fileName: String? = null
): SecureStorage = SecureStorageImpl(context, fileName)

actual fun SecureStorage(): SecureStorage {
    return SecureStorageImpl(AndroidApp.getContext(), null)
}