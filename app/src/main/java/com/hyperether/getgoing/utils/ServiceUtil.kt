package com.hyperether.getgoing.utils

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import com.hyperether.getgoing.location.GGLocationService

class ServiceUtil {

    companion object{
        fun newInstance() = ServiceUtil()
    }

    fun isServiceActive(context:Context):Boolean{
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val services:List<ActivityManager.RunningServiceInfo> = activityManager.getRunningServices(
            Int.MAX_VALUE)
        Log.d("Serv", "${services.size}")
        for (item in services){
            Log.d("Serv", item.service.className)
            if (item.service.className == "com.hyperether.getgoing.location.GGLocationService"){
                Log.d("Serv", "item name: ${item.service.className} GGname: ${GGLocationService::class.qualifiedName}")
                return true
            }
        }
        return false
    }
}