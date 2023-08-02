package com.buisness.bonuscards.api.model

data class CardListResponse(
    val status: String,
    val result: List<CardInfo>,
    val request_count: String
)
