package com.nnbinh.trame.db.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.google.android.gms.maps.model.LatLng

@Entity(
    tableName = "SessionDistance", primaryKeys = ["latitude", "longitude", "sessionId"],
    foreignKeys = [ForeignKey(
        entity = Session::class,
        parentColumns = ["id"],
        childColumns = ["sessionId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class SessionDistance(
    @ColumnInfo(name = "prevLatitude") var prevLatitude: Double,
    @ColumnInfo(name = "preLongitude") var preLongitude: Double,
    @ColumnInfo(name = "latitude") var latitude: Double,
    @ColumnInfo(name = "longitude") var longitude: Double,
    @ColumnInfo(name = "sessionId") var sessionId: Long,
    @ColumnInfo(name = "createdAt") var createdAt: Long,
    @ColumnInfo(name = "distance") var distance: Float, // 2.6 meter
    @ColumnInfo(name = "speed") var speed: Float, // 24 m/s
    @ColumnInfo(name = "duration") var duration: Long, // second
) {
  companion object {
    fun convertToLatLngList(distances: List<SessionDistance>): List<LatLng> {
      val laLngList: MutableList<LatLng> = distances.map {
        LatLng(it.latitude, it.longitude)
      }.toMutableList()

      val startPlace = distances.first()
      laLngList.add(0, LatLng(startPlace.prevLatitude, startPlace.prevLatitude))
      return laLngList
    }
  }
}