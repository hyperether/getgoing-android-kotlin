package com.hyperether.getgoing.ui.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.hyperether.getgoing.App
import com.hyperether.getgoing.R
import com.hyperether.getgoing.SharedPref
import com.hyperether.getgoing.databinding.ActivityLocationBinding
import com.hyperether.getgoing.location.GGLocationService
import com.hyperether.getgoing.model.CBDataFrame
import com.hyperether.getgoing.repository.room.GgRepository
import com.hyperether.getgoing.repository.room.MapNode
import com.hyperether.getgoing.repository.room.Route
import com.hyperether.getgoing.repository.room.RouteAddedCallback
import com.hyperether.getgoing.ui.handler.LocationActivityClickHandler
import com.hyperether.getgoing.ui.handler.MainActivityClickHandler
import com.hyperether.getgoing.utils.Constants
import com.hyperether.getgoing.utils.Constants.OPENED_FROM_LOCATION_ACT
import com.hyperether.getgoing.utils.TimeUtils
import com.hyperether.getgoing.viewmodel.NodeListViewModel
import com.hyperether.getgoing.viewmodel.RouteViewModel
import com.hyperether.toolbox.HyperConst
import kotlinx.android.synthetic.main.activity_location.*
import java.text.SimpleDateFormat
import java.util.*

class LocationActivity : AppCompatActivity(), OnMapReadyCallback, RouteAddedCallback {
    val REQUEST_GPS_SETTINGS = 100
    lateinit var mMap: GoogleMap
    lateinit var routeViewModel: RouteViewModel
    lateinit var nodeListViewModel: NodeListViewModel
    lateinit var route: Route
    lateinit var nodeList: List<MapNode>
    lateinit var dataBinding: ActivityLocationBinding
    private lateinit var cbDataFrameLocal: CBDataFrame
    private lateinit var setGoalButton:Button
    private var mLocTrackingRunning = false
    private var mRouteAlreadySaved = false
    private var trackingStarted = false
    private var sdf:SimpleDateFormat = SimpleDateFormat()
    private var profileId:Int = 0
    private var goalStore:Int = 0
    private lateinit var chronoMeteres:Chronometer
    private lateinit var chronoSpeed:Chronometer
    private lateinit var chronoMeterDuration:Chronometer
    private lateinit var chronoCalories:Chronometer
    private var timeWhenStopped:Long = 0
    private var timeWhenStopedForStorage:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cbDataFrameLocal = CBDataFrame.getInstance()!!
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_location)
        val handler = LocationActivityClickHandler(this)
        dataBinding.clickHandler = handler
        dataBinding.locationViewModel = handler


        routeViewModel = ViewModelProviders.of(this).get(RouteViewModel::class.java)
        val routeObserver = Observer<Route> { newRoute ->
            route = newRoute
            if (route!=null){
                showData(route.length,route.energy,route.avgSpeed)
            }
        }
        routeViewModel.currentRoute.observe(this, routeObserver)

        nodeListViewModel = ViewModelProviders.of(this).get(NodeListViewModel::class.java)
        nodeListViewModel.getNodes()?.observe(this, Observer { newList ->
            nodeList = newList
            mMap.clear()
            drawRoute(nodeList)
        })

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)


        setGoalButton = dataBinding.alBtnSetgoal
        setGoalButton.setOnClickListener(View.OnClickListener {
            //Toast.makeText(this,"i dun been clicked",Toast.LENGTH_SHORT).show()
            SharedPref.newInstance().setSentFromFragmentCode(OPENED_FROM_LOCATION_ACT)
            MainActivityClickHandler(supportFragmentManager).onActivitiesClick(it)
        })
    }

    override fun onStart() {
        super.onStart()
        setVisibilities()
        showData(0.0, 0.0, 0.0)
        setExcercizeType();
        setService()
        dataBinding.alBtnPause.setOnClickListener(View.OnClickListener {
            setServiceStopTracking()
        })
    }

    private fun setServiceStopTracking() {
        stopTracking();
        dataBinding.ibAlReset.isClickable = true
        val drawable2: Drawable? = AppCompatResources.getDrawable(this,R.drawable.ic_light_replay_icon)
        dataBinding.ibAlReset.setImageDrawable(drawable2)
        saveRoute();
    }

    private fun saveRoute() {
        mRouteAlreadySaved = true
        val sharedPref:SharedPref = SharedPref.newInstance()
        if (profileId == Constants.WALK_ID && !sharedPref.doesWalkRouteExist()){
            sharedPref.setWalkRouteExisting(true)
            Log.d(LocationActivity::class.simpleName, "saveRoute: $profileId") // ok
        } else if (profileId == Constants.RUN_ID && !sharedPref.doesRunRouteExist()){
            sharedPref.setRunRouteExisting(true)
            Log.d(LocationActivity::class.simpleName, "saveRoute: $profileId")
        }else if (profileId == Constants.RIDE_ID && !sharedPref.doesRideRouteExist()){
            sharedPref.setRideRouteExisting(true)
            Log.d(LocationActivity::class.simpleName, "saveRoute: $profileId")
        }

    }

    private fun stopTracking() {
        val intent:Intent = Intent(this,GGLocationService::class.java)
        this.stopService(intent)
        timeWhenStopedForStorage = TimeUtils.newInstance().chronometerToMills(dataBinding.chrAlDuration)
        timeWhenStopped = dataBinding.chrAlDuration.base - SystemClock.elapsedRealtime()
        dataBinding.chrAlDuration.stop()
        dataBinding.alBtnStart.visibility= View.VISIBLE
        dataBinding.alBtnPause.visibility= View.GONE
        mLocTrackingRunning = false
    }

    private fun setExcercizeType() {
        val sharedPref:SharedPref = SharedPref.newInstance()
        val i:Int = sharedPref.getClickedTypeShowData2()
        Log.d(LocationActivity::class.simpleName, "setExcercizeType: $i")
        profileId = i
    }

    private fun setService() {
        val sharedPref:SharedPref = SharedPref.newInstance()
        al_btn_start.setOnClickListener(View.OnClickListener {
            val goal:Int = sharedPref.getGoal()
            Log.d(LocationActivity::class.simpleName, "setService: $goal")
            if (goal > 0){
                startTracking(applicationContext)
            }else{
                Toast.makeText(this,"Set Goal First",Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun startTracking(context: Context?) {
        if (!trackingStarted){
            trackingStarted = true
            var datef:Date = Date()
            var date:String = sdf.format(datef)
            val sharedPref:SharedPref = SharedPref.newInstance()
            goalStore = sharedPref.getGoal()
            GgRepository.insertRoute(Route(0,0,0.0,0.0,date,0.0,0.0,profileId,goalStore), this)
        }else{
            startTrackingService(this)
        }
    }

    private fun startTrackingService(context: Context){
        val intent:Intent = Intent(this,GGLocationService::class.java)
        intent.putExtra(HyperConst.LOC_INTERVAL,Constants.UPDATE_INTERVAL)
        intent.putExtra(HyperConst.LOC_FASTEST_INTERVAL,Constants.FASTEST_INTERVAL)
        intent.putExtra(HyperConst.LOC_DISTANCE,Constants.LOCATION_DISTANCE)
        this.startService(intent)
        trackingInProgressViewChanges();
        mLocTrackingRunning = true
        mRouteAlreadySaved = false
    }

    private fun trackingInProgressViewChanges() {
        runOnUiThread(Runnable {
          chronoMeterDuration = dataBinding.chrAlDuration
            chronoMeterDuration.base = SystemClock.elapsedRealtime() + timeWhenStopped
            chronoMeterDuration.start()
            dataBinding.alBtnStart.visibility = View.GONE
            dataBinding.alBtnPause.visibility = View.VISIBLE
            if (mLocTrackingRunning){
                val drawable1: Drawable? = AppCompatResources.getDrawable(this,R.drawable.ic_light_save_icon)
                val drawable2: Drawable? = AppCompatResources.getDrawable(this,R.drawable.ic_light_replay_icon)
                dataBinding.ibAlSave.setImageDrawable(drawable1)
                dataBinding.ibAlSave.isClickable = false
                dataBinding.ibAlReset.setImageDrawable(drawable2)
                dataBinding.ibAlReset.isClickable = false
            }
        })
    }

    override fun onMapReady(p0: GoogleMap) {
        if (((ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission
                    .ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission
                    .ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED))
        ) {

            mMap = p0
            mMap.isMyLocationEnabled = true
            mMap.isTrafficEnabled = false
            mMap.isIndoorEnabled = true
            mMap.isBuildingsEnabled = true
            mMap.uiSettings?.isZoomControlsEnabled = true

            val locationManager = getSystemService(
                Context.LOCATION_SERVICE
            ) as LocationManager

            val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (!gpsEnabled) {
                val dialog = AlertDialog.Builder(this)
                dialog.setCancelable(false)
                dialog.setTitle(R.string.alert_dialog_title)
                dialog.setMessage(getString(R.string.alert_dialog_message))
                dialog.setPositiveButton(
                    R.string.alert_dialog_positive_button
                ) { _, _ ->
                    val i = Intent(ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivityForResult(i, REQUEST_GPS_SETTINGS)
                }

                dialog.setNegativeButton(R.string.alert_dialog_negative_button) { _, _ -> finish() }

                dialog.show()
            }

            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE
            criteria.powerRequirement = Criteria.POWER_LOW
            val bestProvider = locationManager.getBestProvider(criteria, false)
            val location = locationManager.getLastKnownLocation(bestProvider!!)
            // throws error because of authorization error had to kill this method
            zoomOverCurrentLocation(mMap, location)
        } else {
            finish()
        }
    }


    /**
     * This method is used for zooming over user current location or last known location.
     *
     * @param googleMap google map v2
     */
    private fun zoomOverCurrentLocation(googleMap: GoogleMap, location: Location?) {
        val latLng = LatLng(location!!.latitude, location!!.longitude)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15F))
    }

    /**
     * This method draws a route.
     *
     * @param mRoute list of nodes
     */
    private fun drawRoute(mRoute: List<MapNode>) {
        var drFirstPass = true
        var firstNode: MapNode? = null
        var secondNode: MapNode? = null

        // Redraw the whole route
        val it = mRoute.iterator()
        while (it.hasNext()) {
            if (drFirstPass) {
                secondNode = it.next()
                firstNode = secondNode
                drFirstPass = false
            } else {
                firstNode = secondNode
                secondNode = it.next()
            }
            if (firstNode != null)
                drawSegment(firstNode, secondNode)
        }
    }

    /**
     * This method draws a segment of the route and coloring it in accordance with the speed
     *
     * @param firstNode first point of the rout
     * @param secondNode second point of the rout
     */
    private fun drawSegment(firstNode: MapNode, secondNode: MapNode) {
        if (firstNode.latitude != null && firstNode.longitude != null &&
            secondNode.latitude != null && secondNode.longitude != null
        )
            mMap.addPolyline(
                PolylineOptions().geodesic(true)
                    .add(LatLng(firstNode.latitude, firstNode.longitude))
                    .add(LatLng(secondNode.latitude, secondNode.longitude))
                    .width(10f)
                    .color(Color.rgb(0, 255, 0))
            )  // Green color
    }

    private fun setVisibilities() {
        val sharedPref = SharedPref.newInstance()
        if (!sharedPref.isGoalSet()) {
            al_btn_setgoal.visibility = View.VISIBLE
            ib_al_save.visibility = View.GONE
            ib_al_reset.visibility = View.GONE
            al_btn_start.isClickable = false
            chr_al_meters.visibility = View.GONE
            chr_al_duration.visibility = View.GONE
            chr_al_kcal.visibility = View.GONE
            chr_al_speed.visibility = View.GONE
            tv_al_kcal.visibility = View.GONE
            tv_al_duration.visibility = View.GONE
            tv_al_speed.visibility = View.GONE
        } else {
            al_btn_setgoal.visibility = View.GONE
            ib_al_save.visibility = View.VISIBLE
            ib_al_reset.visibility = View.VISIBLE
            al_btn_start.isClickable = true
            chr_al_meters.visibility = View.VISIBLE
            chr_al_duration.visibility = View.VISIBLE
            chr_al_kcal.visibility = View.VISIBLE
            chr_al_speed.visibility = View.VISIBLE
            tv_al_kcal.visibility = View.VISIBLE
            tv_al_duration.visibility = View.VISIBLE
            tv_al_speed.visibility = View.VISIBLE
        }
    }

    private fun showData(distance: Double, kcal: Double, vel: Double) {
        chr_al_kcal.text = String.format("%.02f kcal", kcal)
        if (cbDataFrameLocal.measurementSystemId == 1 ||
                cbDataFrameLocal.measurementSystemId == 2)
            chr_al_meters.text = String.format("%.02f ft", distance * 3.281) // present data in feet
        else
            chr_al_meters.text = String.format("%.02f m", distance)

        chr_al_speed.text = String.format("%.02f m/s", vel)
    }

    override fun onBackPressed() {
        if (mLocTrackingRunning || !mRouteAlreadySaved) {
            val dialog = AlertDialog.Builder(this)
            dialog.setCancelable(false)
            dialog.setTitle(R.string.alert_dialog_title_back_pressed)
            dialog.setMessage(getString(R.string.alert_dialog_message_back_pressed))
            dialog.setPositiveButton(R.string.alert_dialog_positive_back_pressed) { _, _ -> {
                stopService(Intent(App.appCtxt(), GGLocationService::class.java))
                //clearCacheData()
                finish()
            }()}

            dialog.setNegativeButton(getString(R.string.alert_dialog_negative_back_pressed)) { _, _ -> }
            dialog.show()

        } else {
            super.onBackPressed()
        }
    }


    override fun onDestroy() {
        nodeListViewModel.setChronometerLastTime(TimeUtils.newInstance().chronometerToMills(dataBinding.chrAlDuration))
        nodeListViewModel.setBackgroundStartTime(System.currentTimeMillis())
        super.onDestroy()
    }

    //ok je ali ce okinuti samo jednom zbog boolean provere.
    override fun onRouteAdded(id: Long) {
        runOnUiThread(Runnable {
            nodeListViewModel.setRouteId(id)
            routeViewModel.setRouteID(id)
        })
        Log.d(LocationActivity::class.simpleName, "onRouteAdded: from listener")
        startTrackingService(this)
    }
}