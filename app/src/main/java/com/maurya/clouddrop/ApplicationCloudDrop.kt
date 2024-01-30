package com.maurya.clouddrop

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.maurya.clouddrop.util.SharedPreferenceHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ApplicationCloudDrop : Application() {

    @Inject
    lateinit var sharedPreferencesHelper: SharedPreferenceHelper
    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(sharedPreferencesHelper.themeFlag[sharedPreferencesHelper.theme])
    }
}