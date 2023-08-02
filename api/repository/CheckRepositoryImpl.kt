package com.buisness.bonuscards.api.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.buisness.bonuscards.api.CardApi
import com.buisness.bonuscards.api.model.CheckGoodsResponse
import com.buisness.bonuscards.api.model.ChecksResponse
import com.buisness.bonuscards.api.model.TokenResponse
import com.buisness.bonuscards.purchase_history.adapter.Cheque
import com.buisness.bonuscards.service.Constants
import retrofit2.Response

class CheckRepositoryImpl(private val cardApi: CardApi) {

    private val applicationId = Constants.app_id

    suspend fun getRetailedChecks(app_psw: String, discount_card_id: String): Response<ChecksResponse> {
        return cardApi.getRetailChecks(applicationId, app_psw, discount_card_id)
    }

    suspend fun getRetailedCheckGoods(app_psw: String, retail_check_id: String): Response<CheckGoodsResponse> {
        return cardApi.getRetailCheckGoods(applicationId, app_psw, retail_check_id)
    }

    suspend fun  getToken(app_psw: String): Response<TokenResponse> {
        return cardApi.repairToken(applicationId, app_psw)
    }

}