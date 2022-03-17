package com.hyperether.getgoing.repository.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routes")
data class Route(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo val duration: Long,
    @ColumnInfo var energy: Double,
    @ColumnInfo var length: Double,
    @ColumnInfo val date: String,
    @ColumnInfo var avgSpeed: Double,
    @ColumnInfo var currentSpeed: Double,
    @ColumnInfo val activity_id: Int,
    @ColumnInfo val goal: Int
)