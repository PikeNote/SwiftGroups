package org.swg.swiftgroups_app

private val screenResults = hashMapOf<String, Any?>()

fun <T: Any> getScreenResult(key: String): T? {
    val result = screenResults[key]
    screenResults[key] = null

    return result as? T
}

fun setScreenResult(key: String, result: Any) {
    screenResults[key] = result
}