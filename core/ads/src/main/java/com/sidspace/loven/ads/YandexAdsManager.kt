package com.sidspace.loven.ads

import android.app.Activity
import android.app.Application
import com.sidspace.loven.ads.di.YandexRewardedAdManager
import com.yandex.mobile.ads.common.MobileAds
import com.yandex.mobile.ads.instream.MobileInstreamAds
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YandexAdsManager @Inject constructor(
    private val app: Application
) {
    fun initialize() {
        MobileAds.initialize(app) {
            MobileInstreamAds.setAdGroupPreloading(true)
            MobileAds.enableLogging(true)
            println("✅ Yandex Ads SDK initialized")
        }
    }
}
