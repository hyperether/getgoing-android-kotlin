package com.hyperether.getgoing.ui.activity

import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.hyperether.getgoing.R
import com.hyperether.getgoing.SharedPref
import com.hyperether.getgoing.databinding.FragmentShowDataBinding
import com.hyperether.getgoing.listeners.GgOnClickListener
import com.hyperether.getgoing.repository.room.MapNode
import com.hyperether.getgoing.repository.room.Route
import com.hyperether.getgoing.utils.Constants
import com.hyperether.getgoing.utils.ProgressBarBitmap
import com.hyperether.getgoing.viewmodel.RouteViewModel

class ShowDataActivity : AppCompatActivity(),OnMapReadyCallback,GgOnClickListener {

    private var typeClicked: Int = 0
    private lateinit var sharedPref: SharedPref
    private lateinit var mMap: GoogleMap
    private var routes = ArrayList<Route>()
    private lateinit var dataLabel: String
    private var activityId = 0
    private lateinit var routeViewModel: RouteViewModel
    private lateinit var mapFragment: MapFragment
    private lateinit var backButton: ImageButton
    private lateinit var label: TextView
    private lateinit var binding: FragmentShowDataBinding
    private var mapToggleDown:Boolean = false

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
        populateListView()

    }

    private fun populateListView() {
        
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
                    applicationContext,  // theres an error here check it latter
                    route[route.size - 1].goal.toLong(),
                    route[0].length,
                    400, 400, 160f, 220f,
                    20, 0
                )
                binding.`var` = route.get(route.size - 1)
                binding.progress.setImageBitmap(bm)
                binding.recyclerList.smoothScrollToPosition(route.size-1)

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
        binding.ibSdBackBtn.setOnClickListener(View.OnClickListener {
            super.onBackPressed()
        })
        binding.btnToggleMap.setOnClickListener(View.OnClickListener {
            it -> toggleMap()
        })
    }

    private fun toggleMap() {
        if (!mapToggleDown){
            binding.btnToggleMap.animate().rotationBy(180F).setDuration(500)
            mapToggleDown = true
            binding.mapFragmentHolder.animate().scaleYBy(1f).setStartDelay(200).setDuration(500)
            binding.displayMap.animate().y((70f * (Resources.getSystem().displayMetrics.density))).setDuration(500)
            drawSavedRoute();
        }else{
            binding.btnToggleMap.animate().rotationBy(180F).setDuration(500)
            mapToggleDown = true
            binding.mapFragmentHolder.animate().scaleYBy(-1f).setDuration(500)
            binding.displayMap.animate().translationY(0f).setStartDelay(200).setDuration(500)

        }
    }

    private fun drawSavedRoute() {
        mMap.clear()
        var route = binding.`var`
        routeViewModel.getNodeListById(route!!.id).observe(this, Observer { it ->
            var listNodes = it
            if (!listNodes.isEmpty()){
                var iterator:Iterator<MapNode> = listNodes.iterator()
                while (iterator.hasNext()){
                    var pOptions:PolylineOptions = PolylineOptions()
                    pOptions.width(10f)
                        .color(resources.getColor(R.color.light_theme_accent))
                        .geodesic(true)  // kill me now
                    var first:Boolean = true
                    var mapNode: MapNode? = null
                    while (iterator.hasNext()){
                        mapNode = iterator.next()
                        if (first){
                            mMap.addCircle(CircleOptions()
                                .center(LatLng(mapNode.latitude!!.toDouble(), mapNode.longitude!!.toDouble()))
                                .radius(5.0)
                                .fillColor(resources.getColor(R.color.light_theme_accent))
                                .strokeColor(resources.getColor(R.color.transparent_light_theme_accent))
                                .strokeWidth(20f))
                            first = false
                        }
                        pOptions.add(LatLng(mapNode.latitude!!.toDouble(),mapNode.longitude!!.toDouble()))

                    }
                    mMap.addPolyline(pOptions)
                    mMap.addCircle(CircleOptions()
                        .center(LatLng(mapNode?.latitude!!.toDouble(), mapNode.longitude!!.toDouble()))
                        .radius(5.0)
                        .fillColor(resources.getColor(R.color.light_theme_accent))
                        .strokeColor(resources.getColor(R.color.transparent_light_theme_accent))
                        .strokeWidth(20f))
                }

                mMap.addCircle(CircleOptions()
                    .center(LatLng(listNodes.get(0).latitude!!.toDouble(), listNodes.get(0).longitude!!.toDouble()))
                    .radius(5.0)
                    .fillColor(resources.getColor(R.color.light_theme_accent))
                    .strokeColor(resources.getColor(R.color.transparent_light_theme_accent))
                    .strokeWidth(20f))

                mMap.addCircle(CircleOptions()
                    .center(LatLng(listNodes.get(listNodes.size -1 ).latitude!!.toDouble(), listNodes.get(listNodes.size -1).longitude!!.toDouble()))
                    .radius(5.0)
                    .fillColor(resources.getColor(R.color.light_theme_accent))
                    .strokeColor(resources.getColor(R.color.transparent_light_theme_accent))
                    .strokeWidth(20f))
                setCameraView(listNodes)

            }
        })
    }

    private fun setCameraView(listNodes: List<MapNode>) {
        var builder:LatLngBounds.Builder = LatLngBounds.Builder()
        for (node in listNodes){
            builder.include(LatLng(node.latitude!!.toDouble(),node.longitude!!.toDouble()))
        }
        var center:LatLng = builder.build().center
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center,16f))
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
            binding.tvSdLabel.text= getText(R.string.walking)
        } else if (typeClicked == Constants.RIDE_ID) {
            label.text = getText(R.string.activity_cycling)
            activityId = Constants.RIDE_ID
            binding.tvSdLabel.text= getText(R.string.rider_text)

        } else if (typeClicked == Constants.RUN_ID) {
            label.text = getText(R.string.running)
            activityId = Constants.RUN_ID
            binding.tvSdLabel.text= getText(R.string.running)

        } else {
            label.text = ""
            activityId = 0
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap.uiSettings.isZoomControlsEnabled = true
    }

    override fun onClick(x1: Bundle) {
        //val route: Route? = x1.getParcelable<Route>(Constants.BUNDLE_PARCELABLE)
      //  binding.setVar(route)
        // add click event from cycler here
        drawSavedRoute()
    }


}