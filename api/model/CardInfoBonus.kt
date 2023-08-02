package com.buisness.bonuscards.api.model

import java.util.*

data class CardInfoBonus(
    val id: Int,
    val num: String,
    val discount_card_type_id: Int,
    var type: CardType?,
    val partner_id: Int,
    val date_begin: String?,
    val date_end: String?,
    val current_discount_value: Float,
    val sum_current: Float,
    val sum_fit: Float,
    val bonus_sum: Float,
    val updated: String,
    val deleted: Boolean,
    var bonuses: List<BonusInfo>,
    var smallCardLogo: String = ""
)
