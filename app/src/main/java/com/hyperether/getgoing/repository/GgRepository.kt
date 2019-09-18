package com.hyperether.getgoing.repository.room

import androidx.lifecycle.LiveData
import com.hyperether.getgoing.App

object GgRepository {

    private var appDatabase: AppDatabase
    private var nodeDao: NodeDao
    private var nodeListLiveData: LiveData<List<MapNode>>

    init {
        appDatabase = AppDatabase.getInstance(context = App.appCtxt())
        nodeDao = appDatabase.nodeDao()
        nodeListLiveData = nodeDao.getAll()
    }

    fun insert(mapNode: MapNode) {
        nodeDao.insert(mapNode)
    }

    fun getNodesLiveData(): LiveData<List<MapNode>> {
        return nodeListLiveData;
    }

}