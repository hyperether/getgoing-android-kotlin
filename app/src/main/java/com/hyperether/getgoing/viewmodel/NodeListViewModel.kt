package com.hyperether.getgoing.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hyperether.getgoing.App
import com.hyperether.getgoing.repository.room.AppDatabase
import com.hyperether.getgoing.repository.room.MapNode

class NodeListViewModel : ViewModel() {

    val currentNodeList: LiveData<List<MapNode>> by lazy {
        MutableLiveData<List<MapNode>>()
    }

    fun getNodes(): LiveData<List<MapNode>> {
        return AppDatabase.getInstance(App.appCtxt()).nodeDao().getAll()
    }
}