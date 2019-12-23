package com.hyperether.getgoing.ui.handler

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentManager
import com.hyperether.getgoing.R
import com.hyperether.getgoing.ui.activity.MainActivity
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