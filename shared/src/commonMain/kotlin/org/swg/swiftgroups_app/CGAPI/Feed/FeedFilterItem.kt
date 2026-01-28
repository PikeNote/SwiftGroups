package org.swg.swiftgroups_app.CGAPI.Feed

import kotlinx.serialization.Serializable

@Serializable
data class FeedFilterItem(
    val add_separator: Int,
    val buttons: List<Button>,
    val slider: String
)