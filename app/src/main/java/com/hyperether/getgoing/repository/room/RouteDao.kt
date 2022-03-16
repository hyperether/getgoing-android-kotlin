package com.hyperether.getgoing.repository.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

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

}