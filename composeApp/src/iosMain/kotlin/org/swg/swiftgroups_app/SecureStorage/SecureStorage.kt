package org.swg.swiftgroups_app.SecureStorage

import org.swg.swiftgroups_app.SecureStorage.impl.SecureStorageImpl

fun SecureStorage(
    serviceName: String? = null,
    accessGroup: String? = null,
    accessibility: SecureStorageImpl.Accessible = SecureStorageImpl.Accessible.WhenUnlocked
): SecureStorage = SecureStorageImpl(serviceName, accessGroup, accessibility)

actual fun SecureStorage(): SecureStorage = SecureStorageImpl(null, null, SecureStorageImpl.Accessible.WhenUnlocked)