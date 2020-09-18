package com.nnbinh.trame.db.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.nnbinh.trame.data.RecordState
import java.util.Date

@Entity(tableName = "Session")
data class Session(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "createdAt") var createdAt: Long = Date().time,
    @ColumnInfo(name = "recordState") var recordState: String = RecordState.NEW.name,
    @ColumnInfo(name = "duration") var duration: Long = 0, // 3600 second
    @ColumnInfo(name = "distance") var distance: Float = 0.0f, // 2500 m
    @ColumnInfo(name = "avgSpeed") var avgSpeed: Float = 0.0f, // 370 m/s
    @ColumnInfo(name = "currentSpeed") var currentSpeed: Float = 0.0f, // 410 m/s
) {
    @Ignore
    var locations: MutableList<SessionLocation> = arrayListOf()
}