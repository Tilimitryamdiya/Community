package ru.netology.community.application

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp
import ru.netology.community.BuildConfig

@HiltAndroidApp
class CommunityApp: Application() {
    private val MAPKIT_API_KEY = BuildConfig.API_KEY

    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(MAPKIT_API_KEY)
    }
}