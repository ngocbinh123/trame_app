package com.nnbinh.trame.ui.record

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.nnbinh.trame.R
import com.nnbinh.trame.data.KEY_SESSION_ID
import com.nnbinh.trame.data.PERCENT_DEFAULT_ZOOM
import com.nnbinh.trame.databinding.ActivityRecordingBinding
import com.nnbinh.trame.db.table.SessionLocation
import com.nnbinh.trame.extension.setOnSingleClickListener
import com.nnbinh.trame.helper.ServiceHelper
import com.nnbinh.trame.service.TrackLocationService
import com.nnbinh.trame.ui.BaseActivity

class RecordingActivity : BaseActivity(), OnMapReadyCallback {
  companion object {
    fun getIntent(context: Context, sessionId: Long): Intent {
      return with(Intent(context, RecordingActivity::class.java)) {
        this.putExtra(KEY_SESSION_ID, sessionId)
        this.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        this
      }
    }
  }

  private lateinit var map: GoogleMap
  private val viewModel: RecordingVM by lazy {
    ViewModelProvider.AndroidViewModelFactory(application).create(RecordingVM::class.java)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding = DataBindingUtil.setContentView<ActivityRecordingBinding>(
        this, R.layout.activity_recording
    )

    binding.lifecycleOwner = this
    binding.viewModel = viewModel
    binding.imvPause.setOnSingleClickListener(::stopTrackingLocationService)
    binding.imvResume.setOnSingleClickListener(::startTrackingLocationService)
    binding.imvStop.setOnSingleClickListener { this.finish() }

    viewModel.sessionId.value = intent.getLongExtra(KEY_SESSION_ID, -1)
    viewModel.locations.observe(this, {
      it.lastOrNull()?.let { updateLocationOnMap() }
    })

    startTrackingLocationService()
  }

  override fun onMapReady(googleMap: GoogleMap) {
    this.map = googleMap
  }

  override fun onLocationGrant(identifyNumber: Int) {
    loadMapFragment()
  }

  private fun loadMapFragment() {
    val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
    mapFragment.getMapAsync(this)
  }

  private fun stopTrackingLocationService() {
    val intent = Intent(this.application, TrackLocationService::class.java)
    intent.putExtra(KEY_SESSION_ID, viewModel.sessionId.value)
    stopService(intent)
  }

  private fun startTrackingLocationService() {
    if (ServiceHelper.isMyServiceRunning(applicationContext, TrackLocationService::class.java)) {
      stopTrackingLocationService()
    }
    val intent = Intent(this.application, TrackLocationService::class.java)
    intent.putExtra(KEY_SESSION_ID, viewModel.sessionId.value)
    startService(intent)
  }

  private fun updateLocationOnMap() {
    val location: SessionLocation = viewModel.locations.value!!.lastOrNull() ?: return
    val startPlace = LatLng(location.preLongitude, location.preLongitude)
    val endPlace = LatLng(location.latitude, location.longitude)

    map.addMarker(MarkerOptions().position(startPlace))
    map.addMarker(MarkerOptions().position(endPlace))
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(endPlace, PERCENT_DEFAULT_ZOOM))
  }
}