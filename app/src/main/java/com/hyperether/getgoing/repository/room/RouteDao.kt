package com.hyperether.getgoing.repository.room

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.MutableStateFlow

@Dao
interface RouteDao {
    @Query("SELECT * from routes")
    abstract fun getAll(): LiveData<List<Route>>

    @Insert
    abstract fun insertRoute(route: Route): Long

    @Query("SELECT * FROM routes WHERE id = :id")
    abstract fun getRouteById(id: Long): Route

    @Query("SELECT * FROM routes WHERE id = :id")
    fun getRouteByIdAsLiveData(id: Long): LiveData<Route?>?

    @Delete
    abstract fun deleteRoutes(vararg routes: Route)

    @Query("DELETE FROM routes WHERE id = :id")
    abstract fun deleteRouteById(id: Long)

    @Query("SELECT * from routes ORDER BY id DESC LIMIT 1")
    abstract fun getLast(): Route

    @Update
    abstract fun updateRoute(route: Route)

    @Query("SELECT * FROM routes WHERE goal > 0 ORDER BY id DESC LIMIT 1")
    fun getLatestRoute(): Route?
}