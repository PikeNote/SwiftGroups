package org.swg.swiftgroups_app.CGAPI.Events

import kotlinx.serialization.Serializable

@Serializable
data class Club (
    val clubName : String,
    val clubUrl : String
)