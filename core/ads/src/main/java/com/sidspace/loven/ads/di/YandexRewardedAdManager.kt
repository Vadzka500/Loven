package com.sidspace.loven.ads.di

import android.app.Activity
import android.content.Context

import android.util.Log
import com.sidspace.core.data.model.UserManager
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.rewarded.Reward
import com.yandex.mobile.ads.rewarded.RewardedAd
import com.yandex.mobile.ads.rewarded.RewardedAdEventListener
import com.yandex.mobile.ads.rewarded.RewardedAdLoadListener
import com.yandex.mobile.ads.rewarded.RewardedAdLoader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YandexRewardedAdManager @Inject constructor(private val userManager: UserManager) {

    private var rewardedAd: RewardedAd? = null
    private var rewardedAdLoader: RewardedAdLoader? = null
    private var isLoading = false


    fun load(context: Context, adUnitId: String = "R-M-17504927-1") {

        rewardedAdLoader = RewardedAdLoader(context).apply {
            setAdLoadListener(object : RewardedAdLoadListener {
                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d("YandexAd", "✅ Rewarded ad loaded")
                    rewardedAd = ad
                    isLoading = false

                }

                override fun onAdFailedToLoad(error: AdRequestError) {
                    Log.e("YandexAd", "❌ Failed to load ad: ${error.description}")
                    isLoading = false
                }
            })
        }

        val config = AdRequestConfiguration.Builder(adUnitId).build()
        rewardedAdLoader?.loadAd(config)
    }

    fun show(activity: Activity) {
        rewardedAd?.apply {
            setAdEventListener(object : RewardedAdEventListener {
                override fun onAdShown() {
                    Log.d("YandexAd", "🎬 Ad shown")
                }

                override fun onAdFailedToShow(error: com.yandex.mobile.ads.common.AdError) {
                    Log.e("YandexAd", "❌ Failed to show ad: ${error.description}")
                }

                override fun onAdDismissed() {
                    Log.d("YandexAd", "🚪 Ad dismissed")
                    destroy()
                    load(activity)
                }

                override fun onAdClicked() {
                    Log.d("YandexAd", "👆 Ad clicked")
                }

                override fun onAdImpression(impressionData: ImpressionData?) {
                    Log.d("YandexAd", "📊 Impression recorded")
                }

                override fun onRewarded(reward: Reward) {
                    Log.d("YandexAd", "🏆 Reward received: ${reward.amount} ${reward.type}")
                    userManager.plusLife()

                }
            })
            show(activity)
        } ?: run {
            Log.w("YandexAd", "⚠️ Ad not ready, loading new one...")
            load(activity)
        }
    }

    fun destroy() {
        rewardedAd?.setAdEventListener(null)
        rewardedAd = null
        rewardedAdLoader?.setAdLoadListener(null)
        rewardedAdLoader = null
    }
}
