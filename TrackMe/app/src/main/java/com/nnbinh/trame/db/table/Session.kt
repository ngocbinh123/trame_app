package com.nnbinh.trame.db.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Session")
data class Session(
    @PrimaryKey(autoGenerate = true) var id: Long,
    @ColumnInfo(name = "createdAt") var createdAt: String,
    @ColumnInfo(name = "distance")var distance: Float, // 2.6 km
    @ColumnInfo(name = "duration") var duration: String, // 2:32:45
    @ColumnInfo(name = "averageSpeed")var averageSpeed: Float, // 24 km/h
) {
  fun getDistanceStr() = "$distance km"
  fun getAverageSpeedStr() = "$averageSpeed km/h"
}