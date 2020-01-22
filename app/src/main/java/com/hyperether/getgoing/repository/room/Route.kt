package com.hyperether.getgoing.repository.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routes")
data class Route(
    @PrimaryKey val id: Long,
    @ColumnInfo val duration: Long,
    @ColumnInfo val energy: Double,
    @ColumnInfo val length: Double,
    @ColumnInfo val date: String,
    @ColumnInfo val avgSpeed: Double,
    @ColumnInfo val activity_id: Int,
    @ColumnInfo val goal: Int
)