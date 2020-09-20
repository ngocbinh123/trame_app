package com.nnbinh.trame.helper

import android.graphics.Color
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.nnbinh.trame.data.PERCENT_DEFAULT_ZOOM

/**
 * Provide functions:
 * - addStartEndPlaces: add a start place and an end place on Map
 * - moveTo: navigate camera to @param place
 * - drawFullRoute: draw session route from a start place to an end place
 * */
class MapHelper(private val map: GoogleMap) {
  fun addStartEndPlaces(startPlace: LatLng, endPlace: LatLng): GoogleMap {
    map.addMarker(MarkerOptions().position(startPlace).title("Start Place"))
    map.addMarker(MarkerOptions().position(endPlace).title("End Place"))
    return map
  }

  fun moveTo(place: LatLng, zoomPercent: Float = PERCENT_DEFAULT_ZOOM): GoogleMap {
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(place, zoomPercent))
    return map
  }

  /**
   * @param locations: all of locations in Session
   * */
  fun drawFullRoute(locations: List<LatLng>) {
    if (locations.isEmpty()) return
    val polylineOptions = with(PolylineOptions()) {
      this.addAll(locations)
      this.width(5f)
      this.color(Color.BLUE)
      this.geodesic(true)
      this
    }
    map.addPolyline(polylineOptions)
  }
}