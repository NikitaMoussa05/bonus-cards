package com.buisness.bonuscards.purchase_history

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buisness.bonuscards.api.repository.CheckRepositoryImpl
import com.buisness.bonuscards.purchase_history.adapter.Cheque
import com.buisness.bonuscards.service.Constants.app_id
import com.buisness.bonuscards.service.Constants.getParamAppPSV
import com.buisness.bonuscards.service.Constants.getRepairAppPSW
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PurchaseHistoryViewModel(
    private val repository: CheckRepositoryImpl
) : ViewModel() {

    private var token = ""

    private val _cheques = MutableLiveData<List<Cheque>>()
    val cheques: LiveData<List<Cheque>>
        get() = _cheques

    val updatePartnerInfo = MutableLiveData<Boolean>()

    val rnd = Math.random()

    var cardsReceivedCounter = MutableLiveData<Int>()

    init {
        cardsReceivedCounter.value = 0
    }

    fun getCheques(card_id: String, secret: String) {
        val params = listOf("app_id=$app_id&discount_card_id=$card_id")
        val psv = getParamAppPSV(token, secret, params)

        viewModelScope.launch(Dispatchers.IO) {
            delay(getDelay())
            val res = repository.getRetailedChecks(psv, card_id)
            if (res.isSuccessful) {
                val chequesReceived = res.body()
                val myCheques = mutableListOf<Cheque>()
                if (chequesReceived != null) {
                    for (cheque in chequesReceived.result) {
                        myCheques.add(cheque.toCheque())
                    }
                }
                Log.d("TAG", "С карты пришло: " + myCheques.toString())
                _cheques.postValue((cheques.value ?: listOf()) + myCheques)
                Log.d("TAG", "Запостили. Теперь cheques = " + cheques.value.toString())
                // Get bonuses for each card
                //for (card in myCards)
                //    getCardBonuses(id, secret, card.id)
            } else {
                getRepairToken(app_id, secret)
            }
        }
    }

    private fun getDelay(): Long {
        return (1000 * Math.random()).toLong()
    }

    private fun getFullChequeInfo(cheque_id: String, secret: String, date: String) {
        val params = listOf("app_id=$app_id&retail_check_id=$cheque_id")
        val psv = getParamAppPSV(token, secret, params)

        viewModelScope.launch(Dispatchers.IO) {
            val res = repository.getRetailedCheckGoods(psv, cheque_id)
            if (res.isSuccessful) {
                val goods = res.body()
                var purchaseSum = 0f
                var bonusSum = 0f
                if (goods != null) {
                    for (item in goods.result) {
                        purchaseSum += item.sum
                    }
                }
                // _cheques.postValue(myCheques)

                // Get bonuses for each card
                //for (card in myCards)
                //    getCardBonuses(id, secret, card.id)
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

}