package com.hyperether.getgoing.repository.room

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "nodes")
data class MapNode(
    @PrimaryKey(autoGenerate = true) val id: Long = -1,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val velocity: Float? = null,
    val number: Long? = null,
    val routeId: Long? = null
)
