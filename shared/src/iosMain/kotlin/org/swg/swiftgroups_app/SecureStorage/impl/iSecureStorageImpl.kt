package org.swg.swiftgroups_app.SecureStorage.impl

import kotlinx.cinterop.BetaInteropApi
import org.swg.swiftgroups_app.SecureStorage.SecureStorage

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value

import platform.CoreFoundation.CFAutorelease
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFMutableDictionaryRef
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.CFTypeRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanFalse
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.CFBridgingRelease

import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding

import platform.Security.SecItemAdd
import platform.Security.SecItemUpdate
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.errSecItemNotFound
import platform.Security.kSecAttrAccessGroup
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

import platform.darwin.noErr

@OptIn(ExperimentalForeignApi::class)
actual open class SecureStorageImpl(
    private val serviceName: String? = null,
    private val accessGroup: String? = null,
) : SecureStorage {

    @OptIn(ExperimentalForeignApi::class)
    private fun query(vararg pairs: Pair<CFStringRef?, CFTypeRef?>): CFMutableDictionaryRef? {
        val map = pairs.toMap()
        return CFDictionaryCreateMutable(null, map.size.convert(), null, null).apply {
            map.forEach { (key, value) ->
                CFDictionaryAddValue(this, key, value)
            }
        }.also {
            CFAutorelease(it)
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun baseQuery(): CFMutableDictionaryRef? {
        val base = query(
            kSecClass to kSecClassGenericPassword,
            kSecAttrService to CFBridgingRetain(serviceName ?: "default_service")
        )
        base?.apply {
            accessGroup?.let {
                CFDictionaryAddValue(this, kSecAttrAccessGroup, CFBridgingRetain(it))
            }
        }
        return base
    }

    actual override fun clear(): Boolean {
        val query = baseQuery() ?: throw Exception("Failed to create query")
        return if (SecItemDelete(query) == noErr.toInt()) true else throw Exception("Failed to clear storage")
    }

    actual override fun deleteObject(forKey: String): Boolean {
        val query = baseQuery() ?: throw Exception("Failed to create query")
        CFDictionaryAddValue(query, kSecAttrAccount, CFBridgingRetain(forKey))
        return if (SecItemDelete(query) == noErr.toInt()) true else throw Exception("Failed to delete object for key: $forKey")
    }

    actual override fun existsObject(forKey: String): Boolean {
        val query = baseQuery() ?: throw Exception("Failed to create query")
        CFDictionaryAddValue(query, kSecAttrAccount, CFBridgingRetain(forKey))
        CFDictionaryAddValue(query, kSecReturnData, kCFBooleanFalse)
        return SecItemCopyMatching(query, null) == noErr.toInt()
    }

    actual override fun getString(forKey: String, defValue: String?): String? = memScoped {
        val query = baseQuery() ?: throw Exception("Failed to create query")
        CFDictionaryAddValue(query, kSecAttrAccount, CFBridgingRetain(forKey))
        CFDictionaryAddValue(query, kSecReturnData, kCFBooleanTrue)
        CFDictionaryAddValue(query, kSecMatchLimit, kSecMatchLimitOne)
        val result = alloc<CFTypeRefVar>()
        return when (SecItemCopyMatching(query, result.ptr)) {
            noErr.toInt() -> (CFBridgingRelease(result.value) as? NSData)?.let { NSString.create(data = it, encoding = NSUTF8StringEncoding).toString() }
            else -> defValue
        }
    }

    @OptIn(BetaInteropApi::class)
    actual override fun set(key: String, stringValue: String): Boolean {
        val valueNSData = NSString.create(string = stringValue)
            .dataUsingEncoding(NSUTF8StringEncoding)
            ?: throw Exception("Failed to encode value")

        // build the common query (class, service, account)
        val query = baseQuery() ?: throw Exception("Failed to create query")
        CFDictionaryAddValue(query, kSecAttrAccount, CFBridgingRetain(key))

        // try to update existing item
        val updateAttrs = CFDictionaryCreateMutable(
            null, 1.convert(), null, null
        ).apply {
            CFDictionaryAddValue(this, kSecValueData, CFBridgingRetain(valueNSData))
            CFAutorelease(this)
        }

        val updateStatus = SecItemUpdate(query, updateAttrs)
        if (updateStatus == errSecItemNotFound) {
            // no existing item, fall back to add
            CFDictionaryAddValue(query, kSecValueData, CFBridgingRetain(valueNSData))
            val addStatus = SecItemAdd(query, null)
            return addStatus == noErr.toInt()
        }
        return updateStatus == noErr.toInt()
    }
}
