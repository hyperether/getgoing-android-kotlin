package com.hyperether.getgoing.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hyperether.getgoing.SharedPref
import com.hyperether.getgoing.repository.room.GgRepository
import com.hyperether.getgoing.repository.room.MapNode

class NodeListViewModel : ViewModel() {

    val currentNodeList: LiveData<List<MapNode>> by lazy {
        MutableLiveData<List<MapNode>>()
    }

    fun getNodes(): LiveData<List<MapNode>>? {
        return GgRepository.getNodesLiveData()
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
}