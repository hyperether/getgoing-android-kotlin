package com.hyperether.getgoing.repository.room

import android.os.Handler
import android.os.HandlerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hyperether.getgoing.App
import com.hyperether.getgoing.repository.callback.ZeroNodeInsertCallback


object GgRepository {

    private var appDatabase: AppDatabase

    private var nodeDao: NodeDao
    private val routeDao: RouteDao

    private val allNodesById: LiveData<List<MapNode>>? = null
    private var nodeListLiveData: LiveData<List<MapNode>>? = null

    private var mHandler: Handler? = null

    init {
        appDatabase = AppDatabase.getInstance(context = App.appCtxt())
        nodeDao = appDatabase.nodeDao()
        routeDao = appDatabase.routeDao()
    }

    fun insert(mapNode: MapNode) {
        nodeDao.insert(mapNode)
    }

    fun getNodesLiveData(): LiveData<List<MapNode>>? {
        return nodeListLiveData
    }

    fun getLastRoute(): LiveData<Route>? {
        val data: MutableLiveData<Route>? = null

        data?.value = routeDao.getLast()

        return data
    }

    fun insertRouteInit(dbRoute: Route?, nodeList: List<MapNode>, callback: ZeroNodeInsertCallback) {
        getRepoHandler()!!.post {
            val routeId: Long = routeDao.insertRoute(dbRoute!!)
            val route: LiveData<Route?>? = routeDao.getRouteByIdAsLiveData(routeId)

            if (route != null) {
                for (currentNode in nodeList) {
                    nodeDao.insert(
                        MapNode(
                            0, currentNode.latitude, currentNode.longitude,
                            currentNode.velocity, currentNode.number,
                            routeId
                        )
                    )
                }

                callback.onAdded()
            }
        }
    }

    private fun getRepoHandler(): Handler? { //possible refactor
        if (mHandler == null) {
            val mThread = HandlerThread("db-thread")
            mThread.start()
            mHandler = Handler(mThread.looper)
        }
        return mHandler
    }

}