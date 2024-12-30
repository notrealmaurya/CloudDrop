package com.maurya.clouddrop

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.maurya.clouddrop.database.LinkDatabase
import com.maurya.clouddrop.util.SharedPreferenceHelper
import dagger.hilt.android.HiltAndroidApp
import java.util.UUID
import javax.inject.Inject

@HiltAndroidApp
class ApplicationClass : Application() {

    companion object {
        lateinit var instance: ApplicationClass
            private set
        lateinit var appDatabase: LinkDatabase
    }

    @Inject
    lateinit var sharedPreferencesHelper: SharedPreferenceHelper

    override fun onCreate() {
        super.onCreate()
        instance = this
        appDatabase = LinkDatabase.getInstance(applicationContext)

        AppCompatDelegate.setDefaultNightMode(sharedPreferencesHelper.themeFlag[sharedPreferencesHelper.theme])

        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = true
        FirebaseCrashlytics.getInstance().setCustomKey("user_id","${UUID.randomUUID()}")
        FirebaseCrashlytics.getInstance().log("Application started")
    }
}

