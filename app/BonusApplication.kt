package com.buisness.bonuscards.app

import android.app.Application
import com.buisness.bonuscards.di.KoinModules
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named

class BonusApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }


    private fun initKoin() {
        startKoin {
            androidContext(this@BonusApplication)
            modules(KoinModules.appModule)
        }
    }


}