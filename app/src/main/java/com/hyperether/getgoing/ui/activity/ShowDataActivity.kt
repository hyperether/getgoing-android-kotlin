package com.hyperether.getgoing.ui.activity

import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.hyperether.getgoing.R
import com.hyperether.getgoing.SharedPref
import com.hyperether.getgoing.databinding.FragmentShowDataBinding
import com.hyperether.getgoing.repository.room.Route
import com.hyperether.getgoing.utils.Constants
import com.hyperether.getgoing.utils.ProgressBarBitmap
import com.hyperether.getgoing.viewmodel.RouteViewModel

class ShowDataActivity : AppCompatActivity() {

    private var typeClicked: Int = 0
    private lateinit var sharedPref: SharedPref
    private lateinit var mMap: GoogleMap
    private var routes = ArrayList<Route>()
    private lateinit var dataLabel: String
    private val mapToogleDown = false
    private var activityId = 0
    private lateinit var routeViewModel: RouteViewModel
    private lateinit var mapFragment: MapFragment
    private lateinit var backButton: ImageButton
    private lateinit var label: TextView
    private lateinit var binding: FragmentShowDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentShowDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = SharedPref.newInstance()
        label = findViewById(R.id.tv_sd_label)
        fetchSharedPrefData(sharedPref)
        setBackButton()
        initializeViewModel()
        initializeViews()
    }

    private fun initializeViewModel() {
        routeViewModel = ViewModelProvider(this).get(RouteViewModel::class.java)
        routeViewModel.getAllRoutes().observe(
            this
        ) { it ->
            routes.clear()
            var route: List<Route> = it
            if (route.size > 1) {
                for (item in route) {
                    if (item.activity_id == activityId) {
                        routes.add(item)
                    }
                }
            }
            if (route.size == 1) {
                showNoRoutesDialog()
            } else {
                var bm: Bitmap = ProgressBarBitmap.newInstance().getWidgetBitmap(
                    applicationContext,
                    route[route.size - 1].goal.toLong(),
                    route[0].length,
                    400,
                    400,
                    160f,
                    220f,
                    20,
                    0
                )
                binding.`var` = route.get(route.size - 1)
                binding.progress.setImageBitmap(bm)


            }
        }
    }

    private fun showNoRoutesDialog() {
        val sharedPref: SharedPref = SharedPref.newInstance()
        val dialog = AlertDialog.Builder(this)
        dialog.setCancelable(false)
        dialog.setMessage(getString(R.string.alert_dialog_no_routes))
        dialog.setPositiveButton(
            getString(R.string.alert_dialog_positive_button_save_btn),
            DialogInterface.OnClickListener { _, _ ->
                when (activityId) {
                    Constants.WALK_ID -> sharedPref.setWalkRouteExisting(false)
                    Constants.ACTIVITY_RUN_ID -> sharedPref.setRunRouteExisting(false)
                    Constants.ACTIVITY_RIDE_ID -> sharedPref.setRideRouteExisting(false)
                }
                super.onBackPressed()
            })
        dialog.show()
    }

    private fun initializeViews() {

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
        if (typeClicked == Constants.WALK_ID) {
            label.text = getText(R.string.walking)
            activityId = Constants.WALK_ID
        } else if (typeClicked == Constants.RIDE_ID) {
            label.text = getText(R.string.activity_cycling)
            activityId = Constants.RIDE_ID

        } else if (typeClicked == Constants.RUN_ID) {
            label.text = getText(R.string.running)
            activityId = Constants.RUN_ID
        } else {
            label.text = ""
            activityId = 0
        }
    }
}