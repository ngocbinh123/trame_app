package com.nnbinh.trame.db.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.nnbinh.trame.data.SessionState
import java.util.Date

/**
 * Session showed a tracking location overview information of user's tracking
 *
 * @param createdAt : is a created session time
 * @param state : showed state of session to know user is recording or pausing.
 * @param duration : is a recording time total. Unit: second
 * @param distance : is a road that user went
 * @param avgSpeed : is average speed
 * @param currentSpeed :
 *        + state == RECORDING => it's speed of the current distance
 *        + state == PAUSE => it's speed of the last distance
 * */
@Entity(tableName = "Session")
data class Session(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "createdAt") var createdAt: Long = Date().time,
    @ColumnInfo(name = "state") var state: String = SessionState.NEW.name,
    @ColumnInfo(name = "duration") var duration: Long = 0, // 3600 second
    @ColumnInfo(name = "distance") var distance: Float = 0.0f, // 2500 m
    @ColumnInfo(name = "avgSpeed") var avgSpeed: Float = 0.0f, // 370 m/s
    @ColumnInfo(name = "currentSpeed") var currentSpeed: Float = 0.0f, // 410 m/s
) {
    @Ignore
    var distances: MutableList<SessionDistance> = arrayListOf()
}