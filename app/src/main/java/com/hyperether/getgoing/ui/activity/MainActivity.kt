package com.hyperether.getgoing.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.hyperether.getgoing.R
import com.hyperether.getgoing.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val WALK_ID = 1
    private val RUN_ID = 2
    private val RIDE_ID = 3

    public val TYPE = "type"
    private val PERMISSION_CODE = 1;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataBinding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        dataBinding.viewModel = ClickHandler()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), PERMISSION_CODE
            )
        }
    }

    inner class ClickHandler {
        fun onWalk(view: View) {
            val intent = Intent(this@MainActivity, LocationActivity::class.java).apply {
                putExtra(TYPE, WALK_ID)
            }
            startActivity(intent)
        }

        fun onRun(view: View) {
            val intent = Intent(this@MainActivity, LocationActivity::class.java).apply {
                putExtra(TYPE, RUN_ID)
            }
            startActivity(intent)
        }

        fun onRide(view: View) {
            val intent = Intent(this@MainActivity, LocationActivity::class.java).apply {
                putExtra(TYPE, RIDE_ID)
            }
            startActivity(intent)
        }
    }
}