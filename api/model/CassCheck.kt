package com.buisness.bonuscards.api.model

import com.buisness.bonuscards.purchase_history.adapter.Cheque
import java.text.SimpleDateFormat
import java.util.*

data class CassCheck(
    val id : Int,
    val date: String,
    val number: String,
    val customer_order_ref: Int,
    val cashier_id: Int,
    val retail_point_id: Int,
    val kkm_id: Int,
    val held: Boolean,
    val retail_shift_id: Int,
    val seller_id: Int,
    val discount_card_id: Int,
    val partner_id: Int,
    val pay_cash: Float,
    val pay_cashless:Float,
    val online_pay: Boolean,
    val affect_on_store: Boolean,
    val moves_money: Boolean,
    val check_type: Int,
    val nds_include: Int,
    val is_fiscal: Boolean,
    val fiscalization_status: String,
    val buyer_phone: String,
    val buyer_email: String,
    val buyer_name: String,
    val buyer_inn: String,
    val comment: String,
    val updated: String,
    val deleted: Boolean
) {

    fun toCheque(): Cheque {

        return Cheque(
            pay_cash + pay_cashless,
            0f,
            date.dropLast(4)
        )
    }
}
