package com.hyperether.getgoing.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.hyperether.getgoing.R
import com.hyperether.getgoing.SharedPref

class ShowDataActivity : AppCompatActivity() {

    private var typeClicked:Int = 0
    private lateinit var sharedPref:SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_show_data)
        sharedPref = SharedPref.newInstance()
        fetchSharedPrefData(sharedPref)

    }

    private fun fetchSharedPrefData(sharedPref: SharedPref) {
        typeClicked = sharedPref.getClickedTypeShowData()
        Log.d(ShowDataActivity::class.simpleName, "type: $typeClicked") // ok
    }
}