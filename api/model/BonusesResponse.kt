package com.buisness.bonuscards.api.model

data class BonusesResponse(
    val status: String,
    val result: List<BonusInfo>,
    val request_count: String
)
