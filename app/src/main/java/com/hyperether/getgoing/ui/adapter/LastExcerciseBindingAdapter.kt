package com.hyperether.getgoing.ui.adapter

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.dinuscxj.progressbar.CircleProgressBar
import com.hyperether.getgoing.R
import com.hyperether.getgoing.repository.room.Route
import com.hyperether.getgoing.utils.Constants

@BindingAdapter("progress_activity_icon")
fun displayActivityProgressIcon(view: View, pAcId: Int) {
    val drawable: Drawable
    view.visibility = View.VISIBLE
    when (pAcId) {
        Constants.ACTIVITY_WALK_ID -> {
            drawable = view.resources.getDrawable(R.drawable.ic_light_walking_icon_white)
            (view as ImageView).setImageDrawable(drawable)
        }
        Constants.ACTIVITY_RUN_ID -> {
            drawable = view.resources.getDrawable(R.drawable.ic_light_running_icon_white)
            (view as ImageView).setImageDrawable(drawable)
        }
        Constants.ACTIVITY_RIDE_ID -> {
            drawable = view.resources.getDrawable(R.drawable.ic_light_bicycling_icon)
            (view as ImageView).setImageDrawable(drawable)
        }
        else -> view.visibility = View.INVISIBLE
    }
}

@BindingAdapter("progress_activity_name")
fun displayActivityProgressName(view: View, pAcId: Int) {
    var acName = ""
    when (pAcId) {
        Constants.ACTIVITY_WALK_ID -> acName = view.resources.getString(R.string.walking)
        Constants.ACTIVITY_RUN_ID -> acName = view.resources.getString(R.string.running)
        Constants.ACTIVITY_RIDE_ID -> acName = view.resources.getString(R.string.cycling)
    }
    (view as TextView).text = acName
}
