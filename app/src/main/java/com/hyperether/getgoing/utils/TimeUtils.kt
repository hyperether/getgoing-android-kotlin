package com.hyperether.getgoing.utils

import android.widget.Chronometer

class TimeUtils {

    companion object{
        fun newInstance() = TimeUtils()
    }
    fun chronometerToMills(chronometer:Chronometer):Long{
        var currentMilliseconds = 0;
        var chronoText = chronometer.text.toString()
        var array = chronoText.split(":")
        if (array.size == 2){
            currentMilliseconds = (array[0].toInt() * 60 * 1000
                    + array[1].toInt() * 1000)
        }else if (array.size == 3){
            currentMilliseconds =
                array[0].toInt() * 60 * 60 * 1000 + array[1].toInt() * 60 * 1000 + array[2].toInt() * 1000
        }
        return currentMilliseconds.toLong()
    }
}