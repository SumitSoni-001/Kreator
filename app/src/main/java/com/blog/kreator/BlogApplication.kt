package com.blog.kreator

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BlogApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }

}