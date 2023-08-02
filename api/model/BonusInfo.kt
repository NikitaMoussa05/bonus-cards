package com.buisness.bonuscards.api.model

data class BonusInfo(
    val id: Int,
    val discount_card_id: Int,
    val value: Float,
    val active: Boolean,
    val expire_date: String?,
    val updated: String
    )
