package com.buisness.bonuscards.api.model

data class CheckGoodsResponse(
    val status: String,
    val result: List<CheckGoods>,
    val request_count: String
)
