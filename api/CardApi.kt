package com.buisness.bonuscards.api

import com.buisness.bonuscards.api.model.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface CardApi {
    @GET("discountcards.json")
    suspend fun getDiscountCards(@Query("app_id") app_id: Int,
                                 @Query("app_psw") app_psw: String):Response<CardListResponse>

    @GET("discountcards.json")
    suspend fun getDiscountCardsByPartnerId(@Query("app_id") app_id: Int,
                                      @Query("app_psw") app_psw: String,
                                      @Query("partner_id") partner_id: String):Response<CardListResponse>

    @GET("discountcardtypes.json")
    suspend fun getDiscountCardTypes(@Query("app_id") app_id: Int,
                                            @Query("app_psw") app_psw: String):Response<CardTypeResponse>

    @GET("partnercontactinfo.json")
    suspend fun getPartnerContactInfo(@Query("app_id") app_id: Int,
                                      @Query("app_psw") app_psw: String,
                                      @Query("phone") phone: String):Response<PartnerInfoResponse>

    @GET("discountcardbonuses.json")
    suspend fun getCardBonusHistory(@Query("app_id") app_id: Int,
                                            @Query("app_psw") app_psw: String,
                                            @Query("discount_card_id") discount_card_id: Int):Response<BonusesResponse>

    @GET("retailchecks.json")
    suspend fun getRetailChecks(@Query("app_id") app_id: Int,
                                @Query("app_psw") app_psw: String,
                                @Query("discount_card_id") discount_card_id: String): Response<ChecksResponse>

    @GET("retailcheckgoods.json")
    suspend fun getRetailCheckGoods(@Query("app_id") app_id: Int,
                                    @Query("app_psw") app_psw: String,
                                    @Query("retail_check_id") retail_check_id: String): Response<CheckGoodsResponse>


    @GET("repair.json")
    suspend fun repairToken(@Query("app_id") app_id: Int,
                            @Query("app_psw") app_psw: String): Response<TokenResponse>



}