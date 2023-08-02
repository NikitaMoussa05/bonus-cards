package com.buisness.bonuscards.api.repository

import com.buisness.bonuscards.api.CardApi
import com.buisness.bonuscards.api.model.*
import com.buisness.bonuscards.service.Constants.app_id
import retrofit2.Response
import java.math.BigInteger
import java.security.MessageDigest

class CardRepositoryImpl(private val cardApi: CardApi) {

    private val applicationId = app_id

    suspend fun  getCards(app_psw: String): Response<CardListResponse> {
        return cardApi.getDiscountCards(applicationId, app_psw)
    }

    suspend fun  getCardsByPartnerId(app_psw: String, partner_id: String): Response<CardListResponse> {
        return cardApi.getDiscountCardsByPartnerId(applicationId, app_psw, partner_id)
    }

    suspend fun  getPartnerContactInfo(app_psw: String, phone: String): Response<PartnerInfoResponse> {
        return cardApi.getPartnerContactInfo(applicationId, app_psw, phone)
    }

    suspend fun  getCardBonusHistory(app_psw: String, card_id: Int): Response<BonusesResponse> {
        return cardApi.getCardBonusHistory(applicationId, app_psw, card_id)
    }

    suspend fun  getCardType(app_psw: String): Response<CardTypeResponse> {
        return cardApi.getDiscountCardTypes(applicationId, app_psw)
    }

    suspend fun  getToken(app_psw: String): Response<TokenResponse> {
        return cardApi.repairToken(applicationId, app_psw)
    }

}