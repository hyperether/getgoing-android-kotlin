package com.hyperether.getgoing.ui.fragment.tracking

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Chronometer
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.hyperether.getgoing.R
import com.hyperether.getgoing.SharedPref
import com.hyperether.getgoing.databinding.FragmentTrackingBinding
import com.hyperether.getgoing.location.GGLocationService
import com.hyperether.getgoing.model.CBDataFrame
import com.hyperether.getgoing.repository.room.GgRepository
import com.hyperether.getgoing.repository.room.MapNode
import com.hyperether.getgoing.repository.room.Route
import com.hyperether.getgoing.repository.room.RouteAddedCallback
import com.hyperether.getgoing.ui.handler.LocationActivityClickHandler
import com.hyperether.getgoing.utils.Constants
import com.hyperether.getgoing.utils.ServiceUtil
import com.hyperether.getgoing.utils.TimeUtils
import com.hyperether.toolbox.HyperConst
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

class TrackingFragment : Fragment(), OnMapReadyCallback, RouteAddedCallback {
    val viewModel: TrackingFragmentViewModel by viewModels()
    lateinit var binding: FragmentTrackingBinding
    val REQUEST_GPS_SETTINGS = 100
    lateinit var mMap: GoogleMap
    lateinit var route: Route
    lateinit var nodeList: List<MapNode>
    private lateinit var cbDataFrameLocal: CBDataFrame
    private lateinit var setGoalButton: Button
    private var mLocTrackingRunning = false
    private var mRouteAlreadySaved = false
    private var trackingStarted = false
    private var sdf: SimpleDateFormat = SimpleDateFormat()
    private var profileId: Int = 0
    private var goalStore: Int = 0
    private lateinit var chronoMeterDuration: Chronometer
    private var timeWhenStopped: Long = 0
    private var timeWhenStopedForStorage: Long = 0
    private var routeCurrentID: Long = 0;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_tracking,
            null,
            false
        )
        binding.viewModel = viewModel
        val handler = LocationActivityClickHandler(requireContext())
        cbDataFrameLocal = CBDataFrame.getInstance()!!
        val serviceUtil: ServiceUtil = ServiceUtil.newInstance()
        mLocTrackingRunning = serviceUtil.isServiceActive(requireContext())
        trackingStarted = serviceUtil.isServiceActive(requireContext())
        binding.handlerName = handler
        Log.d(ServiceUtil::class.simpleName, "onCreate: $mLocTrackingRunning")
        val routeObserver = Observer<Route> { newRoute ->
            route = newRoute
            Log.d("Observer", "$newRoute")
            showData(route.length, route.energy, route.avgSpeed)
        }
        viewModel.getRouteByIdAsLiveData(routeCurrentID).observe(requireActivity(), routeObserver)

        viewModel.getNodeById()?.observe(requireActivity(), Observer { newList ->
            mMap.clear()
            drawRoute(newList)
        })


        setGoalButton = binding.alBtnSetgoal
        setGoalButton.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.action_trackingFragment2_to_fragmentMyActivities2)
        })

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.viewModel = viewModel
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setVisibilities()
        clearData()
        val serviceUtil: ServiceUtil = ServiceUtil.newInstance()
        if (serviceUtil.isServiceActive(requireContext())) {
            continueTracking()
        }
        showData(0.0, 0.0, 0.0)
        setExcercizeType();
        setService()
        binding.alBtnPause.setOnClickListener(View.OnClickListener {
            setServiceStopTracking()
        })
        binding.ibAlReset.setOnClickListener(View.OnClickListener {
            resetServiceTracking()
        })
        binding.ibAlBackbutton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun continueTracking() {
        trackingInProgressViewChanges()
        val time: Long = viewModel.getChronometerLastTime()
        val backgroundStartTime: Long = viewModel.getBackgroundStartTime()
        binding.chrAlDuration.base =
            (SystemClock.elapsedRealtime() - time - (System.currentTimeMillis() - backgroundStartTime));
        binding.chrAlDuration.start()
        viewModel.continueTracking(requireActivity())
    }

    private fun resetServiceTracking() {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setCancelable(false)
        dialog.setMessage(getString(R.string.alert_dialog_message_reset_btn))
        dialog.setPositiveButton(getString(R.string.alert_dialog_positive_reset_save_btn),
            DialogInterface.OnClickListener { _, _ ->
                mMap.clear()
                binding.chrAlDuration.base = SystemClock.elapsedRealtime()
                timeWhenStopped = 0
                clearData();
                if (!mRouteAlreadySaved) {
                    viewModel.removeRouteById(routeCurrentID)
                }
                mRouteAlreadySaved = true
                trackingStarted = false
                binding.ibAlReset.isClickable = false
                val drawable2: Drawable? =
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.ic_light_replay_icon
                    )
                binding.ibAlReset.setImageDrawable(drawable2)
            })
            .setNegativeButton(getString(R.string.alert_dialog_negative_reset_save_btn),
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
        dialog.show()
    }

    private fun clearData() {
        showData(0.0, 0.0, 0.0)
    }

    private fun setServiceStopTracking() {
        stopTracking();
        binding.ibAlReset.isClickable = true
        val drawable2: Drawable? =
            AppCompatResources.getDrawable(requireContext(), R.drawable.ic_light_replay_icon)
        binding.ibAlReset.setImageDrawable(drawable2)
        saveRoute();
    }

    private fun saveRoute() {
        mRouteAlreadySaved = true
        val sharedPref: SharedPref = SharedPref.newInstance()
        if (profileId == Constants.WALK_ID && !sharedPref.doesWalkRouteExist()) {
            sharedPref.setWalkRouteExisting(true)
        } else if (profileId == Constants.RUN_ID && !sharedPref.doesRunRouteExist()) {
            sharedPref.setRunRouteExisting(true)
        } else if (profileId == Constants.RIDE_ID && !sharedPref.doesRideRouteExist()) {
            sharedPref.setRideRouteExisting(true)
        }
    }

    private fun stopTracking() {
        val intent = Intent(requireActivity(), GGLocationService::class.java)
        requireActivity().stopService(intent)
        timeWhenStopedForStorage =
            TimeUtils.newInstance().chronometerToMills(binding.chrAlDuration)
        timeWhenStopped = binding.chrAlDuration.base - SystemClock.elapsedRealtime()
        binding.chrAlDuration.stop()
        binding.alBtnStart.visibility = View.VISIBLE
        binding.alBtnPause.visibility = View.GONE
        mLocTrackingRunning = false
    }

    private fun setExcercizeType() {
        val sharedPref: SharedPref = SharedPref.newInstance()
        val i: Int = sharedPref.getClickedTypeShowData2()
        profileId = i
    }

    private fun setService() {
        val sharedPref: SharedPref = SharedPref.newInstance()
        binding.alBtnStart.setOnClickListener(View.OnClickListener {
            val goal: Int = sharedPref.getGoal()
            if (goal > 0) {
                startTracking(requireContext())
            } else {
                Toast.makeText(requireContext(), "Set Goal First", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun startTracking(context: Context?) {
        if (!trackingStarted) {
            trackingStarted = true
            val datef: Date = Date()
            val date: String = sdf.format(datef)
            val sharedPref: SharedPref = SharedPref.newInstance()
            goalStore = sharedPref.getGoal()
            GgRepository.insertRoute(
                Route(0, 0, 0.0, 0.0, date, 0.0, 0.0, profileId, goalStore),
                this
            )
        } else {
            startTrackingService(requireContext())
        }
    }

    private fun startTrackingService(context: Context) {
        val intent: Intent = Intent(requireActivity(), GGLocationService::class.java)
        intent.putExtra(HyperConst.LOC_INTERVAL, Constants.UPDATE_INTERVAL)
        intent.putExtra(HyperConst.LOC_FASTEST_INTERVAL, Constants.FASTEST_INTERVAL)
        intent.putExtra(HyperConst.LOC_DISTANCE, Constants.LOCATION_DISTANCE)
        requireActivity().startService(intent)
        trackingInProgressViewChanges();
        mLocTrackingRunning = true
        mRouteAlreadySaved = false
    }

    private fun trackingInProgressViewChanges() {

        lifecycleScope.launch(Dispatchers.Main) {
            chronoMeterDuration = binding.chrAlDuration
            chronoMeterDuration.base = SystemClock.elapsedRealtime() + timeWhenStopped
            chronoMeterDuration.start()
            binding.alBtnStart.visibility = View.GONE
            binding.alBtnPause.visibility = View.VISIBLE
            if (mLocTrackingRunning) {
                val drawable1: Drawable? =
                    AppCompatResources.getDrawable(requireContext(), R.drawable.ic_light_save_icon)
                val drawable2: Drawable? =
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.ic_light_replay_icon
                    )
                binding.ibAlSave.setImageDrawable(drawable1)
                binding.ibAlSave.isClickable = false
                binding.ibAlReset.setImageDrawable(drawable2)
                binding.ibAlReset.isClickable = false
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        if (((ActivityCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission
                    .ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission
                    .ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED))
        ) {

            mMap = p0
            mMap.isMyLocationEnabled = true
            mMap.isTrafficEnabled = false
            mMap.isIndoorEnabled = true
            mMap.isBuildingsEnabled = true
            mMap.uiSettings.isZoomControlsEnabled = true

            val locationManager = requireActivity().getSystemService(
                Context.LOCATION_SERVICE
            ) as LocationManager

            val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (!gpsEnabled) {
                val dialog = AlertDialog.Builder(requireContext())
                dialog.setCancelable(false)
                dialog.setTitle(R.string.alert_dialog_title)
                dialog.setMessage(getString(R.string.alert_dialog_message))
                dialog.setPositiveButton(
                    R.string.alert_dialog_positive_button
                ) { _, _ ->
                    val i = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivityForResult(i, REQUEST_GPS_SETTINGS)
                }

                dialog.setNegativeButton(R.string.alert_dialog_negative_button) { _, _ -> requireActivity().finish() }

                dialog.show()
            }

            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE
            criteria.powerRequirement = Criteria.POWER_LOW
            val bestProvider = locationManager.getBestProvider(criteria, false)
            val location = bestProvider?.let { locationManager.getLastKnownLocation(it) }
            // throws error because of authorization error had to kill this method
            val long =
                bestProvider?.let { locationManager.getLastKnownLocation(it)!!.longitude.toLong() }
            val lati =
                bestProvider?.let { locationManager.getLastKnownLocation(it)!!.latitude.toLong() }

            zoomOverCurrentLocation(mMap, location)
            mMap.setOnMyLocationChangeListener { location ->
                val latLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
            }
        } else {
            requireActivity().finish()
        }
    }


    /**
     * This method is used for zooming over user current location or last known location.
     *
     * @param googleMap google map v2
     */
    private fun zoomOverCurrentLocation(googleMap: GoogleMap, location: Location?) {
        val latLng = location?.let { LatLng(it.latitude, location.longitude) }
        latLng?.let { CameraUpdateFactory.newLatLngZoom(it, 15F) }?.let { googleMap.moveCamera(it) }
    }

    /**
     * This method draws a route.
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
            )
    }

    private fun setVisibilities() {
        val sharedPref = SharedPref.newInstance()
        if (!sharedPref.isGoalSet()) {
            binding.alBtnSetgoal.visibility= View.VISIBLE
            binding.ibAlSave.visibility = View.GONE
            binding.ibAlReset.visibility = View.GONE
            binding.alBtnStart.isClickable = false
            binding.chrAlMeters.visibility = View.GONE
            binding.chrAlDuration.visibility = View.GONE
            binding.chrAlKcal.visibility = View.GONE
            binding.chrAlSpeed.visibility =View.GONE
            binding.tvAlKcal.visibility = View.GONE
            binding.tvAlDuration.visibility = View.GONE
            binding.tvAlSpeed.visibility = View.GONE
        } else {
            binding.alBtnSetgoal.visibility =View.GONE
            binding.ibAlSave.visibility =View.INVISIBLE
            binding.ibAlReset.visibility =View.VISIBLE
            binding.alBtnStart.isClickable = true
            binding.chrAlMeters.visibility =View.VISIBLE
            binding.chrAlDuration.visibility =View.VISIBLE
            binding.chrAlKcal.visibility =View.VISIBLE
            binding.chrAlSpeed.visibility = View.VISIBLE
            binding.tvAlKcal.visibility =View.VISIBLE
            binding.tvAlDuration.visibility = View.VISIBLE
            binding.tvAlSpeed.visibility = View.VISIBLE
        }
    }

    private fun showData(distance: Double, kcal: Double, vel: Double) {
        binding.chrAlKcal.text = String.format("%.02f kcal", kcal)
        if (cbDataFrameLocal.measurementSystemId == 1 ||
            cbDataFrameLocal.measurementSystemId == 2
        )
            binding.chrAlMeters.text = String.format("%.02f ft", distance * 3.281) // present data in feet
        else
            binding.chrAlMeters.text = String.format("%.02f m", distance)
        binding.chrAlSpeed.text = String.format("%.02f m/s", vel)
    }


    override fun onDestroy() {
        viewModel.setChronometerLastTime(
            TimeUtils.newInstance().chronometerToMills(binding.chrAlDuration)
        )
        viewModel.setBackgroundStartTime(System.currentTimeMillis())
        super.onDestroy()
    }

    override fun onRouteAdded(id: Long) {
        routeCurrentID = id
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.setRouteID(routeCurrentID)
        }

        startTrackingService(requireContext())
    }
}