package com.hyperether.getgoing.repository.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NodeDao {
    @Query("SELECT * from nodes")
    fun getAll(): LiveData<List<MapNode>>

    @Query("SELECT * FROM nodes WHERE routeId = :id")
    fun getAllByRouteId(id: Long): LiveData<List<MapNode>>

    @Insert
    fun insert(mapNode: MapNode)

    @Delete
    fun delete(mapNode: MapNode)

    @Delete
    fun deleteNodes(vararg mapNodes: MapNode)
}