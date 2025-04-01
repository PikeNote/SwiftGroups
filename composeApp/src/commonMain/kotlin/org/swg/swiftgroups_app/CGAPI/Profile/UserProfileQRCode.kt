package org.swg.swiftgroups_app.CGAPI.Profile

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileQRCode(
    val balance: String,
    val currency: String,
    val deactivated: String,
    val isAdmin: String,
    val qrcode: String,
    val qrcodeNumber: String,
    val studentID: String,
    val studentUID: String
)