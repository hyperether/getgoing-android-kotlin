package com.hyperether.getgoing.ui.handler

import android.content.Context
import android.view.View
import com.hyperether.getgoing.ui.activity.LocationActivity

class LocationActivityClickHandler(pContext: Context) {
    val mContext = pContext

    fun onBackPressed(view: View) {
        (LocationActivity::onBackPressed).invoke(mContext as LocationActivity) //very questionable, not tested
    }
}