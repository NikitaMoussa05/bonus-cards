package com.buisness.bonuscards.api.model

data class CheckGoods(
    val id: Int,
    val retail_check_id: Int,
    val good_id: Int,
    val amount: Float,
    val price: Float,
    val measure_id: Int,
    val discount_type: Int,
    val discount_value: Float,
    val sum: Float,
    val modification_id: Int,
    val nds_id: Int,
    val payed_sum: Float,
    val pay_type_ffd: Int,
    val updated: String
)
