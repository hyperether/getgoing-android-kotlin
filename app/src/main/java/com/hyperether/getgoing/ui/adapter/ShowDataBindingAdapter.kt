package com.hyperether.getgoing.ui.adapter

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("goal")
fun displayGoalValue(view: View, pGoal: Double) {
    val goal = StringBuilder()
    if (pGoal < 1000) {
        goal.append(pGoal.toInt())
            .append(" m")
    } else {
        goal.append(String.format("%.2f", pGoal / 1000))
            .append(" km")
    }
    (view as TextView).text = goal.toString()
}

@BindingAdapter("displayDistance")
fun displayDistance(view: View, pDist: Double) {
    var dist = 0.0
    dist = if (pDist > 1000) {
        pDist / 1000
    } else {
        pDist
    }
    (view as TextView).text = String.format("%.2f", dist)
}

@BindingAdapter("displayDistanceUnit")
fun displayDistanceUnit(view: View, pDist: Double) {
    val sb = StringBuilder()
    if (pDist > 1000) {
        sb.append("km")
    } else {
        sb.append("m")
    }
    (view as TextView).text = sb
}

@BindingAdapter("displayKcal")
fun displayKcal(view: View, pKcal: Double) {
    var kcal: Long = 0
    kcal = if (pKcal > 1000) {
        Math.round(pKcal / 1000)
    } else {
        Math.round(pKcal)
    }
    (view as TextView).text = kcal.toString() + " kcal"
}