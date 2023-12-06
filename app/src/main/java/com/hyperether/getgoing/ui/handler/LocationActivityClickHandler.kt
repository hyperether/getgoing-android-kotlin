package com.hyperether.getgoing.ui.handler

import android.content.Context
import android.view.View
import com.hyperether.getgoing.R
import com.hyperether.getgoing.model.CBDataFrame
import com.hyperether.getgoing.ui.activity.HomeActivity

class LocationActivityClickHandler(pContext: Context) {
    private val mContext = pContext
    var mTitle: String = when (CBDataFrame.getInstance()?.profileId) {
        1 -> pContext.getString(R.string.activity_walking)
        2 -> pContext.getString(R.string.activity_running)
        3 -> pContext.getString(R.string.activity_cycling)
        else -> ""
    }

    fun onBackPressed(view: View) {
        (HomeActivity::onBackPressed).invoke(mContext as HomeActivity)
    }
}