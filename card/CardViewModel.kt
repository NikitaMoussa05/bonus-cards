package com.buisness.bonuscards.card

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buisness.bonuscards.api.model.*
import com.buisness.bonuscards.api.repository.CardRepositoryImpl
import com.buisness.bonuscards.api.repository.UserAccountRepository
import com.buisness.bonuscards.service.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.concurrent.thread

class CardViewModel(private val repository: CardRepositoryImpl, private val clientNumber: String) :
    ViewModel() {

    private var token = ""

    private val userInfoRepository = UserAccountRepository()

    val clientCards = MutableLiveData<List<CardInfoBonus>>()

    val updateBonuses = MutableLiveData<Boolean>()

    val updatePartnerInfo = MutableLiveData<Boolean>()

    val partnerId = MutableLiveData<String>()

    val cardTypes = MutableLiveData<List<CardType>>()

    val uiInfo: LiveData<UiInfoModel> = userInfoRepository.uiInfo

    val isNoCardsButtonVisible = MutableLiveData<Boolean>()

    init {
        updatePartnerInfo.value = false
        isNoCardsButtonVisible.value = false
        Log.d("Phone", clientNumber)
    }


    fun getCardsByPartnerId(id: Int, secret: String) {
        val currentPartnerId = partnerId.value ?: return // Wrong function call
        val params = listOf("app_id=$id&partner_id=$currentPartnerId")
        val psv = getParamAppPSV(token, secret, params)

        viewModelScope.launch(Dispatchers.IO) {
            val res = repository.getCardsByPartnerId(psv, currentPartnerId)
            if (res.isSuccessful) {
                val cards = res.body()
                val myCards = mutableListOf<CardInfoBonus>()
                if (cards != null) {
                    for (card in cards.result) {
                        myCards.add(card.toBonus())
                    }
                }
                clientCards.postValue(myCards)
                // Get bonuses for each card
                for (card in myCards)
                    getCardBonuses(id, secret, card.id)
            }
        }
    }

    fun getPartnerContactInfo(id: Int, secret: String) {
        val params = listOf("app_id=$id&phone=$clientNumber")
        val psv = getParamAppPSV(token, secret, params)
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("tag", "send request")
            val res = repository.getPartnerContactInfo(psv, clientNumber)
            if (res.isSuccessful) {
                val partnerInfo = res.body()
                if (partnerInfo != null && partnerInfo.result.isNotEmpty()) {
                    Log.d("tag", "id " + partnerInfo.result.first().partner_id)
                    partnerId.postValue(partnerInfo.result.first().partner_id)
                    isNoCardsButtonVisible.postValue(false)
                } else {
                    isNoCardsButtonVisible.postValue(true)
                }
            } else {
                getRepairToken(id, secret)
            }
        }
    }

    private fun getRepairToken(id: Int, secret: String) {
        val psv = getRepairAppPSW(secret, id.toString())
        Log.d("tag", psv)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getToken(psv)

            if (response.body() != null)
                token = response.body()!!.token
            updatePartnerInfo.postValue(true)
        }
    }

    fun getDiscountCardTypes(id: Int, secret: String) {
        val params = listOf("app_id=$id")
        val psv = getParamAppPSV(token, secret, params)
        viewModelScope.launch(Dispatchers.IO) {
            val res = repository.getCardType(psv)
            if (res.isSuccessful) {
                val cardType = res.body()
                if (cardType != null) {
                    cardTypes.postValue(cardType.result)
                }
            } else {
                getRepairToken(id, secret)
            }
        }
    }

    fun getCardBonuses(id: Int, secret: String, cardId: Int) {
        val params = listOf("app_id=$id&discount_card_id=$cardId")
        val psv = getParamAppPSV(token, secret, params)

        viewModelScope.launch(Dispatchers.IO) {
            val res = repository.getCardBonusHistory(psv, cardId)
            if (res.isSuccessful) {
                val bonuses = res.body()
                val cards = clientCards.value ?: return@launch
                val requiredCard = cards.find { it.id == cardId }

                if (bonuses != null && requiredCard != null) {
                    requiredCard.bonuses = bonuses.result
                }
                Log.d("tag", requiredCard.toString())
                updateBonuses.postValue(true)

            }
        }
    }

    private fun getRepairAppPSW(secret: String, app_id: String): String {
        return md5(secret + "app_id=" + app_id)
    }

    private fun getParamAppPSV(token: String, secret: String, params: List<String>): String {
        val strParams = params.joinToString()
        return md5(token + secret + strParams)
    }

    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
}