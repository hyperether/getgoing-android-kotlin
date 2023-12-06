package com.hyperether.getgoing.ui.fragment.showdata

import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hyperether.getgoing.R
import com.hyperether.getgoing.SharedPref
import com.hyperether.getgoing.databinding.FragmentAllRoutesBinding
import com.hyperether.getgoing.repository.room.MapNode
import com.hyperether.getgoing.repository.room.Route
import com.hyperether.getgoing.utils.Constants
import com.hyperether.getgoing.utils.ProgressBarBitmap
import com.hyperether.getgoing.viewmodel.RouteViewModel

class FragmentAllRoutes : Fragment(), OnMapReadyCallback {

    private val viewModel: FragmentAllRouteViewModel by viewModels()
    private lateinit var mapFragment: MapFragment
    private var mapaReady = false
    private lateinit var mMap:GoogleMap
    private lateinit var binding: FragmentAllRoutesBinding
    private var dataLabel: String? = null
    private var activityId = 0
    private lateinit var routeViewModel: RouteViewModel
    private val routes:ArrayList<MapNode> = ArrayList<MapNode>()
    private var recyclerAdapter: RecyclerView.Adapter<*>? = null
    private var mapToogleDown = false
    private var routeRun = mutableListOf<Route>()
    private var routeWalk = mutableListOf<Route>()
    private var routeCycle = mutableListOf<Route>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         binding=DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_all_routes,
            null,
            false
        )
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        dataLabel = requireArguments().getString(DATA_DETAILS_LABEL)
//
//        if (resources.getString(R.string.walking) == dataLabel) {
//            activityId = ACTIVITY_WALK_ID
//        } else if (resources.getString(R.string.running) == dataLabel) {
//            activityId = ACTIVITY_RUN_ID
//        } else {
//            activityId = ACTIVITY_RIDE_ID
//        }

        initializeViewModel()
        initializeViews()
//        mapFragment = (childFragmentManager.findFragmentById(R.id.sd_map_viewSupport) as MapFragment?)!!
//        mapFragment.getMapAsync(this)
    }

    private fun initializeViewModel() {
        routeViewModel = ViewModelProvider(this).get(RouteViewModel::class.java)
        routeViewModel.getAllRoutes().observe(
            requireActivity()
        ) { it ->
            if (!routeWalk.isEmpty()) {
                routeWalk.clear()
            }
            if (!routeRun.isEmpty()) {
                routeRun.clear()
            }
            if (!routeCycle.isEmpty()) {
                routeCycle.clear()
            }
            for (x in it) {
                if (x.activity_id == Constants.WALK_ID) {
                    routeWalk.add(x)
                }
                if (x.activity_id == Constants.RUN_ID) {
                    routeRun.add(x)
                }
                if (x.activity_id == Constants.ACTIVITY_RIDE_ID) {
                    routeCycle.add(x)
                }
            }
            val sharedPref: SharedPref = SharedPref.newInstance()
            val type = sharedPref.getClickedTypeShowData2()
            if (type == Constants.WALK_ID) {
                if (routeWalk.isEmpty()) {
                } else {
                    val bm: Bitmap = ProgressBarBitmap.newInstance().getWidgetBitmap(
                        requireContext(),  // theres an error here check it latter
                        routeWalk[routeWalk.size - 1].goal.toLong(),
                        routeWalk[routeWalk.size - 1].length,
                        400, 400, 160f, 220f,
                        20, 0
                    )
                    binding.data = routeWalk.get(routeWalk.size - 1)
                    binding.progress.setImageBitmap(bm)
                    binding.recyclerList.smoothScrollToPosition(routeWalk.size - 1)
                }
            }
            if (type == Constants.RUN_ID) {
                if (routeRun.isEmpty()) {
                } else {
                    val bm: Bitmap = ProgressBarBitmap.newInstance().getWidgetBitmap(
                        requireContext(),  // theres an error here check it latter
                        routeRun[routeRun.size - 1].goal.toLong(),
                        routeRun[routeRun.size - 1].length,
                        400, 400, 160f, 220f,
                        20, 0
                    )
                    binding.data = routeRun.get(routeRun.size - 1)
                    binding.progress.setImageBitmap(bm)
                    binding.recyclerList.smoothScrollToPosition(routeRun.size - 1)
                }
            }
            if (type == Constants.RIDE_ID) {
                if (routeCycle.isEmpty()) {
//                    showNoRoutesDialog()
                } else {
                    val bm: Bitmap = ProgressBarBitmap.newInstance().getWidgetBitmap(
                        requireContext(),
                        routeCycle[routeCycle.size - 1].goal.toLong(),
                        routeCycle[routeCycle.size - 1].length,
                        400, 400, 160f, 220f,
                        20, 0
                    )
                    binding.data = routeCycle.get(routeCycle.size - 1)
                    binding.progress.setImageBitmap(bm)
                    binding.recyclerList.smoothScrollToPosition(routeCycle.size - 1)
                }
            }
        }
    }

    private fun initializeViews() {
//        binding.tvLabel.setText(dataLabel)
//        binding.ibSdBackBtn.setOnClickListener { v -> requireActivity().onBackPressed() }
//        binding.ibSdDeleteBtn.setOnClickListener { v -> deleteRoute() }
        binding.mapFragmentHolder.visibility = View.GONE
        drawSavedRoute()
    }

    private fun deleteRoute() {
        val dialog = MaterialAlertDialogBuilder(requireActivity())
        dialog.setCancelable(false)
        dialog.setMessage(resources.getString(R.string.alert_dialog_delete_route))
        dialog.setPositiveButton(
            R.string.alert_dialog_positive_button_save_btn
        ) { paramDialogInterface: DialogInterface?, paramInt: Int ->
            routeViewModel.removeRouteById(binding.data!!.id)
            Toast.makeText(activity, "Route deleted", Toast.LENGTH_SHORT).show()
        }
        dialog.setNegativeButton(
            getString(R.string.alert_dialog_negative_button_save_btn)
        ) { paramDialogInterface: DialogInterface?, paramInt: Int -> }
        dialog.show()
    }

//    private fun showNoRoutesDialog() {
//        MaterialAlertDialogBuilder(activity!!)
//            .setCancelable(false)
//            .setMessage(resources.getString(R.string.alert_dialog_no_routes))
//            .setPositiveButton(
//                R.string.alert_dialog_positive_button_save_btn
//            ) { paramDialogInterface: DialogInterface?, paramInt: Int ->
//                when (activityId) {
//                    ACTIVITY_WALK_ID -> SharedPref.setWalkRouteExisting(false)
//                    ACTIVITY_RUN_ID -> SharedPref.setRunRouteExisting(false)
//                    ACTIVITY_RIDE_ID -> SharedPref.setRideRouteExisting(false)
//                }
//                activity!!.onBackPressed()
//            }
//            .show()
//    }

    private fun drawSavedRoute() {
        if (mapaReady){
            mMap.clear()
            val route=binding.data
            routeViewModel.getNodeListById(route!!.id).observe(
                viewLifecycleOwner,
                Observer<List<MapNode>> { dbNodes: List<MapNode>? ->
                    if (dbNodes != null && !dbNodes.isEmpty()) {
                        val it: Iterator<MapNode> = dbNodes.iterator()
                        while (it.hasNext()) {
                            val pOptions = PolylineOptions()
                            pOptions.width(10f)
                                .color(resources.getColor(R.color.light_theme_accent))
                                .geodesic(true)
                            var first = true
                            var node: MapNode? = null
                            while (it.hasNext()) {
                                node = it.next()
                                if (first) {
                                    mMap.addCircle(
                                        CircleOptions()
                                            .center(LatLng(node.latitude!!.toDouble(), node.longitude!!.toDouble()))
                                            .radius(5.0)
                                            .fillColor(resources.getColor(R.color.light_theme_accent))
                                            .strokeColor(resources.getColor(R.color.transparent_light_theme_accent))
                                            .strokeWidth(20f)
                                    )
                                    first = false
                                }
                                pOptions.add(LatLng(node.latitude!!.toDouble(), node.longitude!!.toDouble()))
                            }
                            mMap.addPolyline(pOptions)
                            mMap.addCircle(
                                CircleOptions()
                                    .center(LatLng(node?.latitude!!.toDouble(), node.longitude!!.toDouble()))
                                    .radius(5.0)
                                    .fillColor(resources.getColor(R.color.light_theme_accent))
                                    .strokeColor(resources.getColor(R.color.transparent_light_theme_accent))
                                    .strokeWidth(20f)
                            )
                        }
                        mMap.addCircle(
                            CircleOptions()
                                .center(LatLng(dbNodes[0].latitude!!, dbNodes[0].longitude!!))
                                .radius(5.0)
                                .fillColor(resources.getColor(R.color.light_theme_accent))
                                .strokeColor(resources.getColor(R.color.transparent_light_theme_accent))
                                .strokeWidth(20f)
                        )
                        mMap.addCircle(
                            CircleOptions()
                                .center(
                                    LatLng(
                                        dbNodes[dbNodes.size - 1].latitude!!,
                                        dbNodes[dbNodes.size - 1].longitude!!
                                    )
                                )
                                .radius(5.0)
                                .fillColor(resources.getColor(R.color.light_theme_accent))
                                .strokeColor(resources.getColor(R.color.transparent_light_theme_accent))
                                .strokeWidth(20f)
                        )
                        setCameraView(dbNodes)
                    }
                })
        }
    }

    private fun setCameraView(routeNodes: List<MapNode>) {
        val builder = LatLngBounds.Builder()
        for (node in routeNodes) {
            builder.include(LatLng(node.latitude!!.toDouble(), node.longitude!!.toDouble()))
        }

        // find route center point
        val center = builder.build().center
        // zoom over center
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 16f))
    }

    /**
     * This method is for populating list view
     */




    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mapaReady = true
        mMap.uiSettings.isZoomControlsEnabled = true
    }


}