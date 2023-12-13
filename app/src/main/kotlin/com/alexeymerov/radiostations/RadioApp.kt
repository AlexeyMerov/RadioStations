package com.alexeymerov.radiostations

import android.app.Application
import com.alexeymerov.radiostations.common.NumberedTimberTree
import com.alexeymerov.radiostations.core.common.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class RadioApp : Application() {

    @Inject
    lateinit var analytics: FirebaseAnalytics

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(NumberedTimberTree())
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
        analytics.setAnalyticsCollectionEnabled(!BuildConfig.DEBUG)
    }

}