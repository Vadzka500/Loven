package com.sidspace.loven

import android.app.Application
import com.google.firebase.FirebaseApp
import com.sidspace.loven.ads.YandexAdsManager
import com.sidspace.loven.ads.di.YandexRewardedAdManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class LovenApp: Application(){

    @Inject
    lateinit var yandexAdsManager: YandexAdsManager



    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
        yandexAdsManager.initialize()

    }
}

