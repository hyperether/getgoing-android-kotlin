package com.hyperether.getgoing.room

import androidx.room.Entity
import androidx.room.PrimaryKey

//@ColumnInfo(name = "latitude") val latitude: Double?,
//@ColumnInfo(name = "longitude") val longitude: Double?,
//@ColumnInfo(name = "velocity") val velocity: Float?,
//@ColumnInfo(name = "number") val number: Long?,
//@ColumnInfo(name = "routeId") val routeId: Long?

@Entity(tableName = "nodes")
data class Node(
    @PrimaryKey(autoGenerate = true) val id: Long = -1,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val velocity: Float? = null,
    val number: Long? = null,
    val routeId: Long? = null
)
