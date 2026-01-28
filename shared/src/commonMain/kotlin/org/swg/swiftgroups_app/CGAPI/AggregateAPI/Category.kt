package org.swg.swiftgroups_app.CGAPI.AggregateAPI

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val category_id: Int,
    val name: String
)