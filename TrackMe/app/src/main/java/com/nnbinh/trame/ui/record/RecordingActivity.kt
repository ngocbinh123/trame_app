package com.nnbinh.trame.ui.record

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.nnbinh.trame.R
import com.nnbinh.trame.data.PERCENT_DEFAULT_ZOOM
import com.nnbinh.trame.data.RecordState.NULL
import com.nnbinh.trame.data.RecordState.PAUSE
import com.nnbinh.trame.data.RecordState.RECORDING
import com.nnbinh.trame.data.event.LocationEvent
import com.nnbinh.trame.databinding.ActivityRecordingBinding
import com.nnbinh.trame.extension.setOnSingleClickListener
import com.nnbinh.trame.service.TrackLocationService
import com.nnbinh.trame.ui.BaseActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode.MAIN


class RecordingActivity : BaseActivity(), GoogleMap.OnPolylineClickListener {
  companion object {
    fun getIntent(context: Context): Intent {
      return with(Intent(context, RecordingActivity::class.java)) {
        this.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        this
      }
    }
  }

  private lateinit var map: GoogleMap
  private val viewModel: RecordingVM by lazy {
    ViewModelProvider.AndroidViewModelFactory(application).create(RecordingVM::class.java)
  }

  private var service: TrackLocationService? = null
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    EventBus.getDefault().register(this)
    val binding = DataBindingUtil.setContentView<ActivityRecordingBinding>(
        this, R.layout.activity_recording
    )
    binding.lifecycleOwner = this
    binding.viewModel = viewModel
    binding.imvPause.setOnSingleClickListener {
      viewModel.recordState.value = PAUSE
      service?.stopTracking()
      viewModel.updateSession()
    }

    binding.imvResume.setOnSingleClickListener {
      startService()
    }

    binding.imvStop.setOnSingleClickListener {
      service?.stopTracking()
      viewModel.updateSession()
      this.finish()
    }

    viewModel.lastLocation.observe(this, { updateLocationOnMap() })
  }


  override fun onDestroy() {
    EventBus.getDefault().unregister(this)
    service?.stopTracking()
    super.onDestroy()
  }

  override fun onMapReady(googleMap: GoogleMap) {
    this.map = googleMap
    // Add polylines to the map.
    // Polylines are useful to show a route or some other connection between points.
    // Add polylines to the map.
    // Polylines are useful to show a route or some other connection between points.
    val polyline1 = googleMap.addPolyline(
        PolylineOptions()
            .clickable(true)
            .add(
                LatLng(-35.016, 143.321),
                LatLng(-34.747, 145.592),
                LatLng(-34.364, 147.891),
                LatLng(-33.501, 150.217),
                LatLng(-32.306, 149.248),
                LatLng(-32.491, 147.309)
            )
    )

    // Position the map's camera near Alice Springs in the center of Australia,
    // and set the zoom factor so most of Australia shows on the screen.

    // Position the map's camera near Alice Springs in the center of Australia,
    // and set the zoom factor so most of Australia shows on the screen.
    updateLocationOnMap()
    // Set listeners for click events.

    // Set listeners for click events.
    this.map.setOnPolylineClickListener(this)
  }

  override fun onLocationGrant(identifyNumber: Int) {
    loadMapFragment()
    getCurrentLocation()
    viewModel.createNewSession()
    startService()
  }

  private fun loadMapFragment() {
    val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
    mapFragment.getMapAsync(this)
  }

  override fun onPolylineClick(p0: Polyline?) {
  }

  private fun startService() {
    val intent = Intent(this.application, TrackLocationService::class.java)
    val connection = object : ServiceConnection {
      override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        service = (p1 as? TrackLocationService.TrackLocationBinder)?.getService()
        service?.startTracking()
        viewModel.recordState.value = RECORDING
      }

      override fun onServiceDisconnected(p0: ComponentName?) {
        service = null
        viewModel.recordState.value = NULL
      }
    }
    this.application.startService(intent)
    this.application.bindService(intent, connection, BIND_AUTO_CREATE)
  }

  private fun updateLocationOnMap() {
    viewModel.lastLocation.value?.let { location ->
      val place = LatLng(location.latitude, location.longitude)
      map.addMarker(MarkerOptions().position(place).title(getString(R.string.you_here)))
      map.moveCamera(CameraUpdateFactory.newLatLngZoom(place, PERCENT_DEFAULT_ZOOM))
    }
  }

  @SuppressLint("MissingPermission")
  private fun getCurrentLocation() {
    LocationServices.getFusedLocationProviderClient(this@RecordingActivity).lastLocation
        .addOnSuccessListener { location -> viewModel.lastLocation.value = location }
  }

  @Subscribe(threadMode = MAIN)
  fun onReceiveNewLocation(event: LocationEvent) {
    EventBus.getDefault().removeStickyEvent(event)
    viewModel.onSaveNewLocation(event.location)
  }
}