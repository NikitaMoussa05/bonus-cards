package com.buisness.bonuscards.api.model

data class CardInfo(
    val id: Int,
    val num: String,
    val discount_card_type_id: Int,
    val partner_id: Int,
    val date_begin: String,
    val date_end: String,
    val current_discount_value: Float,
    val sum_current: Float,
    val sum_fit: Float,
    val bonus_sum: Float,
    val updated: String,
    val deleted: Boolean
)

fun CardInfo.toBonus(): CardInfoBonus {
    return CardInfoBonus(
        id,
        num,
        discount_card_type_id,
        null,
        partner_id,
        date_begin,
        date_end,
        current_discount_value,
        sum_current,
        sum_fit,
        bonus_sum,
        updated,
        deleted,
        listOf()
    )
}
