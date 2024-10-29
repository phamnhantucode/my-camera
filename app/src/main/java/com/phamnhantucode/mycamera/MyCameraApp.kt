package com.phamnhantucode.mycamera

import android.app.Application
import com.phamnhantucode.mycamera.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


class MyCameraApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyCameraApp)
            androidLogger()
            modules(appModule)
        }
    }
}