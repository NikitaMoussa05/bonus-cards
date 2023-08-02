package com.buisness.bonuscards.api.model

data class ChecksResponse(
    val status: String,
    val result: List<CassCheck>,
    val request_count: String
)
