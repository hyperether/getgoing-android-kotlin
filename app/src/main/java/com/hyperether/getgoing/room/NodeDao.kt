package com.hyperether.getgoing.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NodeDao {
    @Query("SELECT * from nodes")
    abstract fun getAll(): List<Node>

    @Query("SELECT * FROM nodes WHERE routeId = :id")
    abstract fun getAllByRouteId(id: Long): List<Node>

    @Insert
    fun insert(node: Node)

    @Delete
    fun delete(node: Node)

    @Delete
    abstract fun deleteNodes(vararg nodes: Node)
}