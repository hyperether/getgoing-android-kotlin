package com.hyperether.getgoing.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.hyperether.getgoing.R
import com.hyperether.getgoing.SharedPref
import com.hyperether.getgoing.repository.room.Route
import com.hyperether.getgoing.utils.Constants
import com.hyperether.getgoing.viewmodel.RouteViewModel
import org.w3c.dom.Text

class ShowDataActivity : AppCompatActivity() {

    private var typeClicked:Int = 0
    private lateinit var sharedPref:SharedPref
    private lateinit var mMap:GoogleMap
    private lateinit var routes: List<Route>
    private lateinit var dataLabel:String
    private val mapToogleDown = false
    private val activityId = 0
    private lateinit var routeViewModel:RouteViewModel
    private lateinit var mapFragment: MapFragment
    private lateinit var backButton:ImageButton
    private lateinit var label:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_show_data)
        sharedPref = SharedPref.newInstance()
        fetchSharedPrefData(sharedPref)
        setBackButton()
    }

    private fun setBackButton() {
        backButton = findViewById(R.id.ib_sd_back_btn)
        backButton.setOnClickListener(View.OnClickListener {
            super.onBackPressed()
        })
    }

    private fun fetchSharedPrefData(sharedPref: SharedPref) {
        typeClicked = sharedPref.getClickedTypeShowData()
        Log.d(ShowDataActivity::class.simpleName, "type: $typeClicked") // ok
        label = findViewById(R.id.tv_sd_label)
        if (typeClicked == Constants.WALK_ID){
            label.text = getText(R.string.walking)
        }else if (typeClicked == Constants.RIDE_ID){
            label.text = getText(R.string.activity_cycling)
        }else if (typeClicked == Constants.RUN_ID){
            label.text = getText(R.string.running)
        }else{
            label.text = ""
        }
    }
}