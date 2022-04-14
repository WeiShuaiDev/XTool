package com.linwei.xtool

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class SampleApplication : Application() {

    companion object {

        @SuppressLint("StaticFieldLeak")
        lateinit var appContext: Application;
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }
}