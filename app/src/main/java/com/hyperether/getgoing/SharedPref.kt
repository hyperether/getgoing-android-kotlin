package com.hyperether.getgoing

import android.content.SharedPreferences
import com.hyperether.getgoing.utils.Constants

class SharedPref {

    private val setting:SharedPreferences = App.appCtxt().getSharedPreferences(Constants.PREF_FILE,0)

    companion object{
        fun newInstance() = SharedPref()
    }

    fun test(s:String){
        val editor:SharedPreferences.Editor = setting.edit()
        editor.putString("test",s)
        editor.apply()
    }

    fun setGender(i:Int){
        val editor: SharedPreferences.Editor = setting.edit()
        editor.putInt("gender", i)
        editor.apply()
    }

    fun getGender():Int{
        return setting.getInt("gender",0)
    }

    fun setAge(age:Int){
        val editor:SharedPreferences.Editor = setting.edit()
        editor.putInt("age",age)
        editor.apply()
    }

    fun getAge():Int{
        return setting.getInt("age",0)
    }

    fun setWeight(i:Int){
        val editor:SharedPreferences.Editor = setting.edit()
        editor.putInt("weight",i)
        editor.apply()
    }

    fun getWeight():Int{
        return setting.getInt("weight",0)
    }

    fun setHeight(i:Int){
        val editor:SharedPreferences.Editor = setting.edit()
        editor.putInt("height",i)
        editor.apply()
    }

    fun getHeight():Int{
        return setting.getInt("height",0)
    }

    fun isGenderSet():Boolean{
        return setting.contains("gender")
    }

    fun isGoalSet():Boolean{
        return setting.contains("goal")
    }

    fun setGoal(i:Int){
        val editor:SharedPreferences.Editor = setting.edit()
        editor.putInt("goal",i)
        editor.apply()
    }

    fun getGoal():Int{
        return setting.getInt("goal",0)
    }

    fun walkRouteExisting(walk:Boolean):Boolean{
        return setting.getBoolean(Constants.PREF_WALK_ROUTE_EXISTING,walk)
    }

    fun doesWalkRouteExist():Boolean{
        return setting.getBoolean(Constants.PREF_WALK_ROUTE_EXISTING,false)
    }

    fun setRunRouteExisting(run:Boolean){
        val editor:SharedPreferences.Editor = setting.edit()
        editor.putBoolean(Constants.PREF_RUN_ROUTE_EXISTING,run)
        editor.apply()
    }

    fun doesRunRouteExist():Boolean{
        return setting.getBoolean(Constants.PREF_RUN_ROUTE_EXISTING,false)
    }

    fun setRideRouteExisting(ride:Boolean){
        val editor:SharedPreferences.Editor = setting.edit()
        editor.putBoolean(Constants.PREF_RIDE_ROUTE_EXISTING,ride)
        editor.apply()
    }

    fun doesRideRouteExist():Boolean{
        return setting.getBoolean(Constants.PREF_RIDE_ROUTE_EXISTING,false)
    }

    fun isZeroNodeInit():Boolean{
        return setting.getBoolean("zeroNode",false)
    }

    fun setZeroNodeInit():Boolean{
        return setting.getBoolean("zeroNode",false)
    }

    fun getMeasurementSystemId():Int{
        return setting.getInt("measurementSystemId",Constants.METRIC)
    }

    fun setMeasurementSystemId(i:Int){
        val editor:SharedPreferences.Editor = setting.edit()
        editor.putInt("measurementSystemId",i)
        editor.apply()
    }

    fun setLastTime(time:Long){
        val editor:SharedPreferences.Editor = setting.edit()
        editor.putLong("gg_last_time",time)
        editor.apply()
    }

    fun getLastTime():Long{
        return setting.getLong("gg_last_time",0)
    }

    fun getBackgroundStartTime():Long{
        return setting.getLong("gg_background_time",0)
    }

    fun setBackgroundStartTime(currentTimeMillis:Long){
        val editor:SharedPreferences.Editor = setting.edit()
        editor.putLong("gg_background_time",currentTimeMillis)
        editor.apply()
    }

    fun setSentFromFragmentCode(code:Int){
        val editor:SharedPreferences.Editor = setting.edit()
        editor.putInt("fragmentSentCode",code)
        editor.apply()
    }

    fun getSentFromFragmentCode():Int{
        return setting.getInt("fragmentSentCode",Constants.SENT_FROM_FRAGMENT)
    }


}