package org.bridgwatercarnival.companion

import android.app.Application
import android.content.Context

class AppContext : Application() {
    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
} 