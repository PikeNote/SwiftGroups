package org.swg.swiftgroups_app.CGAPI.Profile

import kotlinx.serialization.Serializable

@Serializable
data class Job(
    val company: String,
    val company_logo: String,
    val industry: String,
    val period: String,
    val title: String,
    val workDescription: String
)