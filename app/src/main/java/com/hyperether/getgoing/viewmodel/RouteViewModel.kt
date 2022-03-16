package com.hyperether.getgoing.viewmodel

import android.app.Activity
import android.app.Application
import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.hyperether.getgoing.App
import com.hyperether.getgoing.repository.room.GgRepository
import com.hyperether.getgoing.repository.room.MapNode
import com.hyperether.getgoing.repository.room.Route
import org.w3c.dom.Node

class RouteViewModel : ViewModel() {

    val currentRoute: MutableLiveData<Route> by lazy {
        MutableLiveData<Route>()
    }

    fun getLatestRoute(): LiveData<Route>? {
        return GgRepository.getLastRoute()
    }

    private lateinit var routeList:LiveData<List<Route>>
    private var routeId:MutableLiveData<Long> = MutableLiveData()
    private var route:LiveData<Route> = Transformations.switchMap(routeId, Function { input -> GgRepository.getRouteByIdAsLiveData(input)})


    fun getRouteByIdAsLiveData(id:Long):LiveData<Route>{
        return route
    }

    fun setRouteID(id: Long) {
        routeId.setValue(id)
    }

    fun getAllRoutes():LiveData<List<Route>>{
        routeList = GgRepository.getAllRoutes()
        return routeList
    }

    fun getNodeListById(id:Long):LiveData<List<MapNode>>{
        return GgRepository.getAllNodesById(id)
    }

    fun removeRouteById(id:Long){
        GgRepository.deleteNodesByRouteId(id)
        GgRepository.deleteRouteById(id)
    }


}