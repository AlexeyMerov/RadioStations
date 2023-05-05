package com.example.radiostations.data.remote.response

data class ResponseWrapper(
    val body: List<Body>,
    val head: Head
)

data class Head(
    val status: String,
    val title: String
)

data class Body(
    val URL: String,
    val element: String,
    val key: String,
    val text: String,
    val type: String
)