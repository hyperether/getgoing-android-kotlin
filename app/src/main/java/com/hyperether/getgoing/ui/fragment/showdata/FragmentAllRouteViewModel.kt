package com.hyperether.getgoing.ui.fragment.showdata

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hyperether.getgoing.repository.room.GgRepository
import com.hyperether.getgoing.repository.room.MapNode
import com.hyperether.getgoing.repository.room.Route

class FragmentAllRouteViewModel(application: Application): AndroidViewModel(application) {

    private val routeList: LiveData<List<Route>>? = null
    private val routeID = MutableLiveData<Long>()
    private val route: MutableLiveData<Route> = MutableLiveData<Route>()

    fun getRouteByIdAsLiveData(): LiveData<Route>? {
        return route
    }

    fun setRouteID(id: Long) {
        routeID.value = id
        GgRepository.getRouteByIdAsLiveData(id)?.observeForever { dbRoute ->
            route.postValue(
                dbRoute
            )
        }
    }
    fun getNodeListById(id: Long): LiveData<List<MapNode>> {
        return GgRepository.getAllNodesById(id)
    }
}