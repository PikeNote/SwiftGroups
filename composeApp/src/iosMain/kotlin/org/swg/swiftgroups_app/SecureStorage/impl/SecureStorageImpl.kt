package org.swg.swiftgroups_app.SecureStorage.impl

import kotlinx.cinterop.BetaInteropApi
import org.swg.swiftgroups_app.SecureStorage.SecureStorage

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import kotlinx.cinterop.reinterpret

import platform.CoreFoundation.CFAutorelease
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFStringCreateWithCString
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.CFTypeRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanFalse
import platform.CoreFoundation.kCFBooleanTrue
import platform.CoreFoundation.kCFStringEncodingUTF8
import platform.Foundation.CFBridgingRelease

import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding

import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.SecItemUpdate
import platform.Security.kSecAttrAccessGroup
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlock
import platform.Security.kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
import platform.Security.kSecAttrAccessibleWhenPasscodeSetThisDeviceOnly
import platform.Security.kSecAttrAccessibleWhenUnlocked
import platform.Security.kSecAttrAccessibleWhenUnlockedThisDeviceOnly
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

import platform.darwin.noErr
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
actual open class SecureStorageImpl(
    private val serviceName: String? = null,
    private val accessGroup: String? = null,
    private val accessibility: Accessible = Accessible.WhenUnlocked
) : SecureStorage {

    enum class Accessible(val value: CFStringRef?) {
        WhenPasscodeSetThisDeviceOnly(kSecAttrAccessibleWhenPasscodeSetThisDeviceOnly),
        WhenUnlockedThisDeviceOnly(kSecAttrAccessibleWhenUnlockedThisDeviceOnly),
        WhenUnlocked(kSecAttrAccessibleWhenUnlocked),
        AfterFirstUnlock(kSecAttrAccessibleAfterFirstUnlock),
        AfterFirstUnlockThisDeviceOnly(kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly)
    }

    actual override fun clear(): Boolean {
        val query = createBaseQuery()
        val status = SecItemDelete(query)
        return status == noErr.toInt()
    }

    actual override fun deleteObject(forKey: String): Boolean {
        val query = createBaseQuery().apply {
            CFDictionaryAddValue(this, kSecAttrAccount, forKey.toCFString())
        }
        val status = SecItemDelete(query)
        return status == noErr.toInt()
    }

    @OptIn(BetaInteropApi::class)
    actual override fun set(key: String, stringValue: String): Boolean {
        val data = stringValue.toNSData()
        return addOrUpdate(key, data)
    }

    actual override fun existsObject(forKey: String): Boolean {
        val query = createBaseQuery().apply {
            CFDictionaryAddValue(this, kSecAttrAccount, forKey.toCFString())
            CFDictionaryAddValue(this, kSecReturnData, kCFBooleanFalse)
        }

        val status = SecItemCopyMatching(query, null)
        return status == noErr.toInt()
    }

    actual override fun data(forKey: String): ByteArray? {
        val query = createBaseQuery().apply {
            CFDictionaryAddValue(this, kSecAttrAccount, forKey.toCFString())
            CFDictionaryAddValue(this, kSecReturnData, kCFBooleanTrue)
            CFDictionaryAddValue(this, kSecMatchLimit, kSecMatchLimitOne)
        }

        memScoped {
            val result = alloc<CFTypeRefVar>()
            val status = SecItemCopyMatching(query, result.ptr)
            if (status == noErr.toInt()) {
                val nsData = CFBridgingRelease(result.value) as? NSData
                return nsData?.toByteArray()
            }
        }
        return null
    }

    // HELPERS

    private fun addOrUpdate(key: String, data: NSData?): Boolean {
        return if (existsObject(forKey = key)) {
            update(key, data)
        } else {
            add(key, data)
        }
    }

    private fun update(key: String, data: NSData?): Boolean {
        if (data == null) return false

        val query = createBaseQuery().apply {
            CFDictionaryAddValue(this, kSecAttrAccount, key.toCFString())
        }

        val attributesToUpdate = CFDictionaryCreateMutable(null, 1, null, null).apply {
            val cfData: CFTypeRef? = CFBridgingRetain(data)
            CFDictionaryAddValue(this, kSecValueData, cfData)
        }

        val status = SecItemUpdate(query, attributesToUpdate)
        return status == noErr.toInt()
    }

    private fun add(key: String, data: NSData?): Boolean {
        if (data == null) return false

        val query = createBaseQuery().apply {
            CFDictionaryAddValue(this, kSecAttrAccount, key.toCFString())
        }

        val attributesToAdd = CFDictionaryCreateMutable(null, 1, null, null)?.apply {
            val cfData: CFTypeRef? = CFBridgingRetain(data)
            CFDictionaryAddValue(this, kSecValueData, cfData)
        } ?: return false

        val status = SecItemAdd(query, attributesToAdd.reinterpret())
        return status == noErr.toInt()
    }

    private fun createBaseQuery(): CFDictionaryRef = memScoped {
        val dictionary = CFDictionaryCreateMutable(null, 0, null, null)
            ?: error("Failed to create CFMutableDictionary")

        CFDictionaryAddValue(dictionary, kSecClass, kSecClassGenericPassword)

        val cfServiceName = serviceName?.toCFString() ?: "default_service".toCFString()
        CFDictionaryAddValue(dictionary, kSecAttrService, cfServiceName)
        CFDictionaryAddValue(dictionary, kSecAttrAccessible, accessibility.value)

        accessGroup?.let {
            val cfAccessGroup = it.toCFString()
            CFDictionaryAddValue(dictionary, kSecAttrAccessGroup, cfAccessGroup)
        }

        CFAutorelease(dictionary)
        return@memScoped dictionary
    }


    private fun String.toCFString(): CFStringRef? = memScoped {
        CFStringCreateWithCString(null, this@toCFString, kCFStringEncodingUTF8)
    }

    @kotlinx.cinterop.BetaInteropApi
    private fun String.toNSData(): NSData? =
        NSString.create(string = this).dataUsingEncoding(NSUTF8StringEncoding)


    private fun NSData.toByteArray(): ByteArray =
        ByteArray(length.toInt()).apply {
            if (isNotEmpty()) {
                usePinned {
                    memcpy(it.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
                }
            }
        }
}