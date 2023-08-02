package com.buisness.bonuscards.api.model

data class CardTypeResponse(
    val status: String,
    val result: List<CardType>,
    val request_count: String
)
