package com.hyperether.getgoing.ui.handler

import android.view.View
import androidx.fragment.app.FragmentManager
import com.hyperether.getgoing.ui.fragment.ActivitiesFragment
import com.hyperether.getgoing.ui.fragment.ProfileFragment


class MainActivityClickHandler(pManager: FragmentManager) {
    private val mManager = pManager

    fun onProfileClick(view: View) {
        val profileFragment = ProfileFragment()
        profileFragment.show(mManager, "ProfileFragment")
    }

    fun onActivitiesClick(view: View) {
        val activitiesFragment = ActivitiesFragment()
        activitiesFragment.show(mManager, "ActivitiesFragment")
    }


}