package org.swg.swiftgroups_app.SecureStorage

interface SecureStorage {
    /**
     * Saves a string value in the store.
     * @param key The key to store
     * @param stringValue The value to store
     */
    fun set(key: String, stringValue: String): Boolean

    /**
     * Checks if object with key exists in the store.
     * @param forKey The key to query
     * @return True or false, depending on wether it is in the store or not
     */
    fun existsObject(forKey: String): Boolean

    /**
     * Deletes object with the given key from the store.
     * @param forKey The key to query
     * @return True or false, depending on whether the object has been deleted
     */
    fun deleteObject(forKey: String): Boolean

    /**
     * Deletes all objects from the store.
     * @return True or false, depending on whether the objects have been deleted
     */
    fun clear(): Boolean

    /**
     * Returns the data value of an object in the store.
     * @param forKey The key to query
     * @return The stored bytes value
     */
    fun getString(forKey: String, defValue: String? = null): String?
}

expect fun SecureStorage() : SecureStorage