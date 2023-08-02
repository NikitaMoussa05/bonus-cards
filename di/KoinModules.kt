package com.buisness.bonuscards.di

import com.buisness.bonuscards.api.CardApi
import com.buisness.bonuscards.api.Network
import com.buisness.bonuscards.api.repository.CheckRepositoryImpl
import com.buisness.bonuscards.purchase_history.PurchaseHistoryViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object KoinModules {
    val appModule = module {
        single { Network.getRetrofit() }
        single<CardApi> { Network.getApi(retrofit = get()) }
        single { CheckRepositoryImpl(cardApi = get()) }
        viewModel { PurchaseHistoryViewModel(get()) }
    }
}