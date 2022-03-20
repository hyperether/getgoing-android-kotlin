package com.hyperether.getgoing.utils

import android.util.Log
import com.hyperether.getgoing.SharedPref

//love this class
class CaloriesCalculation {
    val kcalMatrix = arrayOf(
        doubleArrayOf(0.89, 0.00078),
        doubleArrayOf(1.12, 0.00074),
        doubleArrayOf(1.34, 0.00073),
        doubleArrayOf(1.56, 0.00071),
        doubleArrayOf(1.79, 0.00078),
        doubleArrayOf(2.01, 0.00087),
        doubleArrayOf(2.23, 0.00099),
        doubleArrayOf(2.68, 0.00104),
        doubleArrayOf(3.13, 0.00102),
        doubleArrayOf(3.58, 0.00105),
        doubleArrayOf(4.02, 0.00104),
        doubleArrayOf(4.47, 0.00112)
    )

    companion object{
        fun newInstance() = CaloriesCalculation()
    }

    /**
     * This method calculate burned calories.
     * @param dis    distance traveled
     * @param vel    velocity
     * @param weight user weight
     */
    fun calculate(dis:Double,vel:Double,pId:Int,weight:Double):Double{
        var energySpent:Double = 0.0
        Log.d(CaloriesCalculation::class.java.simpleName, "calculate: $dis $vel $pId $weight")

        val sharedPref:SharedPref = SharedPref.newInstance()
        var weight2 = weight * 0.4536 // changed this java version has no clear point where it sets measurementSystemId to something else then 0

        if (pId == 1 || pId == 2){  // walking and running
            if (vel < kcalMatrix[0][0]){
                // uses first entry in matrix if speed is less then it
                energySpent = dis * (kcalMatrix[0][1]*weight2)
                Log.d(CaloriesCalculation::class.java.simpleName, "velocity less: $energySpent")
            }else if(vel > kcalMatrix[11][0]){
                energySpent = dis * (kcalMatrix[11][1]*weight2)
                Log.d(CaloriesCalculation::class.java.simpleName, "velocity more: $energySpent")
            }else{
                for (i in 1..12 step 1){
                    if (vel < kcalMatrix[i][0]){
                        energySpent = dis * (kcalMatrix[i][1]*weight2)
                        break
                    }
                }
            }

        }else{ // cycling
            energySpent = if (vel < 4.47) {
                dis * (0.000779 * weight) // riding velocity lower than 16 km/h
            } else {
                dis * (0.001071 * weight) // riding velocity higher than 16 km/h
            }

        }
        return energySpent
    }


}