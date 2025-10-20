package com.sidspace.loven.ads.di

import android.app.Application
import com.sidspace.core.data.model.UserManager
import com.sidspace.loven.ads.YandexAdsManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AdsModule {

    @Provides
    @Singleton
    fun provideYandexAdsManager(app: Application, adManager: YandexRewardedAdManager): YandexAdsManager {
        return YandexAdsManager(app)
    }

    @Provides
    @Singleton
    fun provideYandexRewardedAdManager(userManager: UserManager): YandexRewardedAdManager =
        YandexRewardedAdManager(userManager)
}
