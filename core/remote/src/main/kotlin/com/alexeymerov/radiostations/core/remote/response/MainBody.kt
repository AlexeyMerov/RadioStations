package com.alexeymerov.radiostations.core.remote.response

interface ServerBodyType

/**
 * Since server does not return an appropriate response, we bonded to use wrapper... 'good' old times.
 * */
data class MainBody<T : ServerBodyType>(
    val head: HeadBody,
    val body: List<T>
)

data class HeadBody(
    val status: String,
    val title: String? = null,
)
