package com.maurya.clouddrop

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.maurya.clouddrop.database.LinkDatabase
import com.maurya.clouddrop.database.LinkRepositoryForSavingInDB
import com.maurya.clouddrop.util.SharedPreferenceHelper
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
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


    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        instance = this
        appDatabase = LinkDatabase.getInstance(applicationContext)

        AppCompatDelegate.setDefaultNightMode(sharedPreferencesHelper.themeFlag[sharedPreferencesHelper.theme])

        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = true
        FirebaseCrashlytics.getInstance().setCustomKey("user_id","${UUID.randomUUID()}")
        FirebaseCrashlytics.getInstance().log("Application started")

        applicationScope.launch {
            val twentyFourHoursInMillis = 24 * 60 * 60 * 1000
            val currentTimeMillis = System.currentTimeMillis()
            val allItems = LinkRepositoryForSavingInDB.getLinkFromDatabaseAsync()
            val itemsToRemove =
                allItems.filter { currentTimeMillis - it.createdAt > twentyFourHoursInMillis }
            for (record in itemsToRemove) {
                LinkRepositoryForSavingInDB.deleteSingleLink(record.id)
            }
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        applicationScope.cancel()
    }
}

