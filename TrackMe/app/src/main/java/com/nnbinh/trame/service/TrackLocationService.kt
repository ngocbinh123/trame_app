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
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.nnbinh.trame.R
import com.nnbinh.trame.data.KEY_SESSION_ID
import com.nnbinh.trame.data.LOCATION_DISTANCE
import com.nnbinh.trame.data.LOCATION_INTERVAL
import com.nnbinh.trame.data.ONE_SECOND
import com.nnbinh.trame.data.SessionState
import com.nnbinh.trame.data.SessionState.PAUSE
import com.nnbinh.trame.repo.TrackingLocationServiceRepo

class TrackLocationService : Service() {
  private var repo: TrackingLocationServiceRepo? = null
  private var handler: Handler = Handler()
  private var manager: LocationManager? = null
  private var locationListener: TrackLocationListener = TrackLocationListener()


  override fun onBind(intent: Intent?): IBinder? = null

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    super.onStartCommand(intent, flags, startId)
    val sessionId = intent?.getLongExtra(KEY_SESSION_ID, -1) ?: -1
    repo = TrackingLocationServiceRepo(applicationContext, sessionId) { stopSelf() }
    startTracking()
    startCountUpDurationTime()
    return START_STICKY
  }

  override fun onDestroy() {
    repo?.updateSessionState(PAUSE)
    super.onDestroy()
    handler.removeCallbacksAndMessages(null)
    manager?.removeUpdates(locationListener)
  }

  private fun startTracking() {
    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
      Toast.makeText(applicationContext, R.string.not_enough_permissions, Toast.LENGTH_SHORT).show()
      stopSelf()
      return
    }

    locationListener = TrackLocationListener()
    manager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    manager?.requestLocationUpdates(GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
        locationListener)
  }

  private fun startCountUpDurationTime() {
    handler.postDelayed({
      repo?.updateSessionDuration()
      if (repo?.session?.state == SessionState.RECORDING.name) {
        startCountUpDurationTime()
      } else {
        stopSelf()
      }
    }, ONE_SECOND.toLong())
  }

  inner class TrackLocationListener : LocationListener {

    override fun onLocationChanged(location: Location?) {
      if (location == null) return
      repo?.saveNewLocation(location)
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
    }

    override fun onProviderEnabled(p0: String?) {
    }

    override fun onProviderDisabled(p0: String?) {
    }
  }
}