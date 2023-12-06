package com.hyperether.getgoing.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.hyperether.getgoing.R

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_home)

        //Navigation between fragments
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_home) as NavHostFragment
        val navController = navHostFragment.navController
    }

}