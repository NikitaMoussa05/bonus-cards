package com.buisness.bonuscards.shops

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.buisness.bonuscards.api.model.UiInfoModel
import com.buisness.bonuscards.api.repository.UserAccountRepository
import com.buisness.bonuscards.shops.adapter.CityWithShops

class ShopsViewModel(
    private val repository: UserAccountRepository
): ViewModel() {

    val cityWithShops: LiveData<List<CityWithShops>> = repository.shops

    val uiInfo: LiveData<UiInfoModel> = repository.uiInfo

}