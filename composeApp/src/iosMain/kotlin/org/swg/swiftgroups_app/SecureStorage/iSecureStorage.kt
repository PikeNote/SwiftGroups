package org.swg.swiftgroups_app.SecureStorage

import org.swg.swiftgroups_app.SecureStorage.impl.SecureStorageImpl

actual fun SecureStorage(): SecureStorage = SecureStorageImpl(null, null)