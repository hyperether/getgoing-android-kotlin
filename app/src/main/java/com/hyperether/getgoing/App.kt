package com.hyperether.getgoing

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.HandlerThread

class App : Application() {

    private lateinit var handler:Handler

    init {
        instance = this
    }

    companion object {
        private var instance: App? = null

        fun appCtxt(): Context {
            return instance!!.applicationContext
        }

    }

    fun getHandler():Handler{
        if (handler == null) {
            val thread = HandlerThread("db-thread")
            thread.start()
            handler = android.os.Handler(thread.looper)
        }
        return handler
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}