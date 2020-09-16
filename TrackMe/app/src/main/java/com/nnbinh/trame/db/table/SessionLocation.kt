package com.nnbinh.trame.db.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "SessionLocation", primaryKeys = ["latitude", "longitude", "sessionId"],
    foreignKeys = [ForeignKey(
        entity = Session::class,
        parentColumns = ["id"],
        childColumns = ["sessionId"],
        onDelete = ForeignKey.CASCADE
    )])
data class SessionLocation(
    @ColumnInfo(name = "prevLatitude") var prevLatitude: Double,
    @ColumnInfo(name = "preLongitude") var preLongitude: Double,
    @ColumnInfo(name = "latitude") var latitude: Double,
    @ColumnInfo(name = "longitude") var longitude: Double,
    @ColumnInfo(name = "sessionId") var sessionId: Long,
    @ColumnInfo(name = "createdAt") var createdAt: Long,
    @ColumnInfo(name = "distance")var distance: Double, // 2.6 meter
    @ColumnInfo(name = "speed")var speed: Double, // 24 m/s
    @ColumnInfo(name = "duration")var duration: Long, // second
)