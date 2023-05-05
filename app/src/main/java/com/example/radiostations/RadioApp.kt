package com.example.radiostations

import android.app.Application
import com.example.radiostations.common.NumberedTimberTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber


@HiltAndroidApp
class RadioApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(NumberedTimberTree())
    }

}