package com.hyperether.getgoing.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.hyperether.getgoing.App
import com.hyperether.getgoing.SharedPref
import com.hyperether.getgoing.repository.room.GgRepository
import com.hyperether.getgoing.repository.room.MapNode

class NodeListViewModel : ViewModel() {

    private val routeID = MutableLiveData<Long>()
    private val nodeByRouteId: MutableLiveData<List<MapNode>> = MutableLiveData<List<MapNode>>()
    private val nodesByRouteId: LiveData<List<MapNode>> =
        Transformations.switchMap<Long, List<MapNode>>(
            routeID
        ) { input -> GgRepository.getAllNodesById(input) }

    fun setRouteID(id: Long) {
        routeID.value = id
        GgRepository.getNodesById(id).observeForever { dbNodes ->
            nodeByRouteId.postValue(dbNodes)
        }
    }
    fun getNodes(): LiveData<List<MapNode>>? {
        return GgRepository.getNodesLiveData()
    }

    fun getNodeById(): LiveData<List<MapNode>>? {
        return nodeByRouteId
    }
    fun getNodesById(id: Long): LiveData<List<MapNode>> {
        return GgRepository.getNodesById(id)
    }

    fun setChronometerLastTime(time: Long) {
        SharedPref.newInstance().setLastTime(time)
    }

    fun getBackgroundStartTime(): Long {
        return SharedPref.newInstance().getBackgroundStartTime()
    }

    fun setBackgroundStartTime(currentTimeMillis: Long) {
        SharedPref.newInstance().setBackgroundStartTime(currentTimeMillis)
    }

    fun getChronometerLastTime(): Long {
        return SharedPref.newInstance().getLastTime()
    }

    fun setRouteId(id: Long) {
        routeID.value = id
    }

    fun continueTracking(activity: Activity) {
        App.getHandler().post(Runnable {
            val id: Long = GgRepository.getLastRoute2()!!.id
            activity.runOnUiThread(Runnable {
                setRouteId(id)
                getNodesById(id)
            })
        })
    }
}