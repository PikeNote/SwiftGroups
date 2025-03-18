package org.swg.swiftgroups_app.SecureStorage.impl

import org.swg.swiftgroups_app.SecureStorage.SecureStorage

expect open class SecureStorageImpl : SecureStorage {
    override fun clear(): Boolean

    override fun deleteObject(forKey: String): Boolean

    override fun set(key: String, stringValue: String): Boolean

    override fun existsObject(forKey: String): Boolean

    override fun getString(forKey: String, defValue: String?): String?
}