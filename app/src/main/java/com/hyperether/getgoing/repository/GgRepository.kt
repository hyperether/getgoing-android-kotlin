package com.hyperether.getgoing.repository.room

import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hyperether.getgoing.App
import com.hyperether.getgoing.repository.callback.ZeroNodeInsertCallback
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.atomic.AtomicLong


object GgRepository {

    private var appDatabase: AppDatabase

    private var nodeDao: NodeDao
    private val routeDao: RouteDao
    private lateinit var allNodes: LiveData<List<MapNode>>
    private lateinit var allNodesById: LiveData<List<MapNode>>
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


    fun daoInsertNode(node: MapNode?) {
        getRepoHandler()!!.post { nodeDao.insertNode(node) }
    }

    fun insertRoute(route: Route, listener: RouteAddedCallback) {
        val routeId = AtomicLong()
        getRepoHandler()?.post(Runnable {
            routeId.set((routeDao.insertRoute(route)))
            listener.onRouteAdded(routeId.get())
        })
    }

    fun getNodesLiveData(): LiveData<List<MapNode>>? {
        return nodeListLiveData
    }

    fun getLastRoute(): LiveData<Route>? {
        val data: MutableLiveData<Route>? = null

        data?.value = routeDao.getLast()

        return data
    }

    fun getLastRoute2(): Route? {
        return routeDao.getLatestRoute()
    }

    fun updateRoute(route: Route) {
        getRepoHandler()?.post(Runnable {
            routeDao.updateRoute(route)
        })
    }

    fun getNodesById(id: Long): LiveData<List<MapNode>> {
        return nodeDao.getAllByRouteId(id)
    }

    fun insertRouteInit(
        dbRoute: Route?,
        nodeList: List<MapNode>,
        callback: ZeroNodeInsertCallback
    ) {
        getRepoHandler()!!.post {
            val routeId: Long = routeDao.insertRoute(dbRoute!!)
            Log.d("PROVERA STA IMA OVDE", routeId.toString())
            val route: LiveData<Route?>? = routeDao.getRouteByIdAsLiveData(routeId)
            // TODO: Ne radi ovo ovde ne dobijam routeId

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

    fun insertRouteInitMainActivity(route: Route, nodeList: List<MapNode>) {
        getRepoHandler()!!.post(Runnable {
            var routeId: Long
            routeId = routeDao.insertRoute(route)
            var route: LiveData<Route?>? = routeDao.getRouteByIdAsLiveData(routeId)
            if (route != null) {
                for (item in nodeList) {
                    daoInsertNode(
                        MapNode(
                            0, item.latitude, item.longitude, item.velocity, item.number, routeId
                        )
                    )
                }
            }
        })
    }

    private fun getRepoHandler(): Handler? { //possible refactor
        if (mHandler == null) {
            val mThread = HandlerThread("db-thread")
            mThread.start()
            mHandler = Handler(mThread.looper)
        }
        return mHandler
    }

    fun getRouteByIdAsLiveData(id: Long): LiveData<Route?>? {
        return routeDao.getRouteByIdAsLiveData(id)
    }

    fun getAllRoutes(): LiveData<List<Route>> {
        return routeDao.getAll()
    }


    fun getAllNodesById(id: Long): LiveData<List<MapNode>> {
        allNodesById = nodeDao.getAllByRouteIdAsLiveData(id)
        return allNodesById
    }


    fun deleteNodesByRouteId(id: Long) {
        getRepoHandler()!!.post(Runnable { nodeDao.deleteAllByRouteId(id) })
    }

    fun deleteRouteById(id: Long) {
        getRepoHandler()!!.post(Runnable { routeDao.deleteRouteById(id) })
    }

}