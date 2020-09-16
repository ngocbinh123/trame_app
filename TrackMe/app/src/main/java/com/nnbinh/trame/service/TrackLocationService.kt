package com.nnbinh.trame.service

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.nnbinh.trame.data.LOCATION_DISTANCE
import com.nnbinh.trame.data.LOCATION_INTERVAL
import com.nnbinh.trame.data.event.LocationEvent
import org.greenrobot.eventbus.EventBus

class TrackLocationService : Service() {
  private val binder: TrackLocationBinder by lazy { TrackLocationBinder() }
  private var manager: LocationManager? = null
  private var locationListener: TrackLocationListener = TrackLocationListener()

  override fun onBind(intent: Intent?): IBinder? = binder

  override fun onCreate() {
    super.onCreate()
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    super.onStartCommand(intent, flags, startId)
    return START_NOT_STICKY
  }

  override fun onDestroy() {
    super.onDestroy()
    manager?.removeUpdates(locationListener)
  }

  fun startTracking() {
    if (ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      Toast.makeText(applicationContext, "Not enough permissions to get location", Toast.LENGTH_SHORT).show()
      return
    }

    locationListener = TrackLocationListener()
    manager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    manager?.requestLocationUpdates(GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
        locationListener)
  }

  fun stopTracking() {
    this.onDestroy()
  }

  inner class TrackLocationListener : LocationListener {
    private val TAG = "TrackLocationListener"

    override fun onLocationChanged(location: Location?) {
      Log.d(TAG, "LocationChanged: $location")

      if (location == null) return
      EventBus.getDefault().post(LocationEvent(location))
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
      Log.d(TAG, "onStatusChanged: $p0")
    }

    override fun onProviderEnabled(p0: String?) {
      Log.d(TAG, "onProviderEnabled: $p0")
    }

    override fun onProviderDisabled(p0: String?) {
      Log.d(TAG, "onProviderDisabled: $p0")
    }
  }

  inner class TrackLocationBinder : Binder() {
    fun getService(): TrackLocationService = this@TrackLocationService
  }
}