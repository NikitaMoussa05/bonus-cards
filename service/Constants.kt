package com.buisness.bonuscards.service

import java.math.BigInteger
import java.security.MessageDigest

object Constants {
    const val account_name = "usdrug"
    const val app_id = 181151
    const val secret = "gIOg4bUYGmAJnUJHTqlUuUXnqIZxNYD3"
    const val haveBottomMenu = false
    const val mainWebLink = "https://komipetsopt.b-catalog.ru/"

    fun getRepairAppPSW(secret: String, app_id: String): String {
        return md5(secret + "app_id=" + app_id)
    }

    fun getParamAppPSV(token: String, secret: String, params: List<String>): String {
        val strParams = params.joinToString()
        return md5(token + secret + strParams)
    }

    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
}