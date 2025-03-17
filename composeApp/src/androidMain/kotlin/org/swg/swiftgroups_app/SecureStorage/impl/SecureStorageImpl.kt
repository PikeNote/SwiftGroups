package org.swg.swiftgroups_app.SecureStorage.impl

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import org.swg.swiftgroups_app.SecureStorage.SecureStorage

actual open class SecureStorageImpl (
    context: Context,
    fileName: String? = null
) : SecureStorage {

    private val encSharedPrefs: SharedPreferences

    init {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build();

        encSharedPrefs = EncryptedSharedPreferences.create(
                context,
            fileName ?: "secure-shared-preferences",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );

    }

    @SuppressLint("UseKtx")
    actual override fun clear(): Boolean {
        return encSharedPrefs
            .edit()
            .clear()
            .commit()
    }

    actual override fun deleteObject(forKey: String): Boolean {
        return encSharedPrefs
            .edit()
            .remove(forKey)
            .commit()
    }

    @SuppressLint("UseKtx")
    actual override fun set(key: String, stringValue: String) : Boolean {
        return encSharedPrefs
            .edit()
            .putString(key, stringValue)
            .commit()
    }

    actual override fun existsObject(forKey: String): Boolean {
        return encSharedPrefs
            .contains(forKey)
    }

    actual override fun data(forKey: String): ByteArray? {
        return encSharedPrefs
            .getString(forKey, null)?.let {
                Base64.decode(it, Base64.DEFAULT)
            }
    }
}