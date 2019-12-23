package com.hyperether.getgoing.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hyperether.getgoing.repository.room.GgRepository
import com.hyperether.getgoing.repository.room.Route

class RouteViewModel : ViewModel() {

    val currentRoute: MutableLiveData<Route> by lazy {
        MutableLiveData<Route>()
    }

    fun getLatestRoute(): LiveData<Route>? {
        return GgRepository.getLastRoute()
    }
}