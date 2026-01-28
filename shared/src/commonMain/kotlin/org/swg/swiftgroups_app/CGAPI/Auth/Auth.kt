package org.swg.swiftgroups_app.CGAPI.Auth

import kotlinx.serialization.Serializable

@Serializable
data class Auth(
    val base_url: String,
    val mobile_token: String,
    val student_id: String,
    val success: Boolean
)