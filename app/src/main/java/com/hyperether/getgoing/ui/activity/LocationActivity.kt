package com.hyperether.getgoing.ui.activity

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
import com.hyperether.getgoing.databinding.ActivityLocationBinding
import com.hyperether.getgoing.location.GGLocationService
import com.hyperether.getgoing.model.CBDataFrame
import com.hyperether.getgoing.repository.room.MapNode
import com.hyperether.getgoing.repository.room.Route
import com.hyperether.getgoing.ui.handler.LocationActivityClickHandler
import com.hyperether.getgoing.viewmodel.NodeListViewModel
import com.hyperether.getgoing.viewmodel.RouteViewModel
import kotlinx.android.synthetic.main.activity_location.*

class LocationActivity : AppCompatActivity(), OnMapReadyCallback {
    val REQUEST_GPS_SETTINGS = 100
    lateinit var mMap: GoogleMap
    lateinit var routeViewModel: RouteViewModel
    lateinit var nodeListViewModel: NodeListViewModel
    lateinit var route: Route
    lateinit var nodeList: List<MapNode>
    lateinit var dataBinding: ActivityLocationBinding

    private lateinit var cbDataFrameLocal: CBDataFrame

    private var mLocTrackingRunning = false
    private var mRouteAlreadySaved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_location)
        dataBinding.clickHandler = LocationActivityClickHandler(this)

        routeViewModel = ViewModelProviders.of(this).get(RouteViewModel::class.java)
        val routeObserver = Observer<Route> { newRoute ->
            route = newRoute
        }
        routeViewModel.currentRoute.observe(this, routeObserver)

        nodeListViewModel = ViewModelProviders.of(this).get(NodeListViewModel::class.java)
        nodeListViewModel.getNodes().observe(this, Observer { newList ->
            nodeList = newList
            mMap.clear()
            drawRoute(nodeList)
        })

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)

        cbDataFrameLocal = CBDataFrame.getInstance()!!
    }

    override fun onStart() {
        super.onStart()

        setVisibilities()
        showData(0.0, 0.0, 0.0)
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
            mMap.setMyLocationEnabled(true)
            mMap.setTrafficEnabled(false)
            mMap.setIndoorEnabled(true)
            mMap.setBuildingsEnabled(true)
            mMap.getUiSettings()?.setZoomControlsEnabled(true)

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
                ) { paramDialogInterface, paramInt ->
                    val i = Intent(ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivityForResult(i, REQUEST_GPS_SETTINGS)
                }

                dialog.setNegativeButton(
                    R.string.alert_dialog_negative_button,
                    object : DialogInterface.OnClickListener {
                        override fun onClick(paramDialogInterface: DialogInterface, paramInt: Int) {
                            finish()
                        }
                    })

                dialog.show()
            }

            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE
            criteria.powerRequirement = Criteria.POWER_LOW
            val bestProvider = locationManager.getBestProvider(criteria, false)
            val location = locationManager.getLastKnownLocation(bestProvider!!)
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

    @Suppress("UNUSED_CHANGED_VALUE")
    private fun setVisibilities() {
        var cnt = 0

        if (cnt++ == 0) {
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

}