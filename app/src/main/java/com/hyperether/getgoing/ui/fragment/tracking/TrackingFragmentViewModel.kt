package com.hyperether.getgoing.ui.fragment.tracking

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.hyperether.getgoing.App
import com.hyperether.getgoing.SharedPref
import com.hyperether.getgoing.databinding.FragmentTrackingBinding
import com.hyperether.getgoing.repository.room.GgRepository
import com.hyperether.getgoing.repository.room.MapNode
import com.hyperether.getgoing.repository.room.Route

class TrackingFragmentViewModel(application: Application) : AndroidViewModel(application) {


    // MutableLiveData za pristup binding objektu
    private val _binding = MutableLiveData<FragmentTrackingBinding>()
    val binding: LiveData<FragmentTrackingBinding> get() = _binding

    private var routeId = MutableLiveData<Long>()
    private val route: MutableLiveData<Route> = MutableLiveData<Route>()

    private var routeIdNode = MutableLiveData<Long>()

    fun setBinding(binding: FragmentTrackingBinding) {
        _binding.value = binding
    }

    private val nodeByRouteId: MutableLiveData<List<MapNode>> = MutableLiveData<List<MapNode>>()

    private val nodesByRouteId: LiveData<List<MapNode>> =
        Transformations.switchMap<Long, List<MapNode>>(
            routeId
        ) { input -> GgRepository.getAllNodesById(input) }

    fun getRouteByIdAsLiveData(id: Long): LiveData<Route> {
        return route
    }

    fun getNodeById(): LiveData<List<MapNode>>? {
        return nodeByRouteId
    }

    fun getChronometerLastTime(): Long {
        return SharedPref.newInstance().getLastTime()
    }

    fun getBackgroundStartTime(): Long {
        return SharedPref.newInstance().getBackgroundStartTime()
    }

    fun continueTracking(activity: Activity) {
        App.getHandler().post(Runnable {
            val id: Long = GgRepository.getLastRoute2()!!.id
            activity.runOnUiThread(Runnable {
                setRouteID(id)
                getNodesById(id)
            })
        })
    }

    private fun getNodesById(id: Long): LiveData<List<MapNode>> {
        return GgRepository.getAllNodesById(id)
    }


    fun removeRouteById(id: Long) {
        GgRepository.deleteNodesByRouteId(id)
        GgRepository.deleteRouteById(id)
    }

    fun setActivitiesName() {
        GgRepository.getLastRoute2()?.activity_id
    }

    fun setRouteID(id: Long) {
        routeId.value = id
        GgRepository.getRouteByIdAsLiveData(id)?.observeForever { dbRoute ->
            route.value=dbRoute
        }
    }
//    fun setNodeID(id:Long){
//        routeIdNode.value=id
//        GgRepository.getRouteByIdAsLiveData(id)?.observeForever{
//        routeIdNode.value =it
//    }

    fun setChronometerLastTime(time: Long) {
        SharedPref.newInstance().setLastTime(time)
    }

    fun setBackgroundStartTime(currentTimeMillis: Long) {
        SharedPref.newInstance().setBackgroundStartTime(currentTimeMillis)
    }


}