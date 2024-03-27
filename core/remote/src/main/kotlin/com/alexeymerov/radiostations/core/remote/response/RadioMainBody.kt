package com.alexeymerov.radiostations.core.remote.response

import kotlinx.serialization.Serializable


@Serializable
data class RadioMainBody<T>(
    val head: HeadBody,
    val body: List<T>
)

@Serializable
data class HeadBody(
    val status: String,
    val title: String? = null,
)
