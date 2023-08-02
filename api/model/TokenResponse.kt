package com.buisness.bonuscards.api.model

import java.net.CacheRequest

data class TokenResponse(
    val token: String,
    val request_count: String,
    val app_psw: String
)
