package com.nnbinh.trame.ui.record

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.nnbinh.trame.R
import com.nnbinh.trame.data.KEY_SESSION_ID
import com.nnbinh.trame.data.SessionState
import com.nnbinh.trame.databinding.ActivityRecordingBinding
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

  private var map: GoogleMap? = null
  private val viewModel: RecordingVM by lazy {
    ViewModelProvider.AndroidViewModelFactory(application).create(RecordingVM::class.java)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding = DataBindingUtil.setContentView<ActivityRecordingBinding>(
        this, R.layout.activity_recording)

    binding.lifecycleOwner = this
    binding.viewModel = viewModel
    binding.imvPause.setOnSingleClickListener(::stopTrackingLocationService)
    binding.imvResume.setOnSingleClickListener(::startTrackingLocationService)
    binding.imvStop.setOnSingleClickListener { this.finish() }

    viewModel.sessionId.value = intent.getLongExtra(KEY_SESSION_ID, -1)
    viewModel.distances.observe(this, { map?.let { m -> viewModel.drawRoute(m) } })
    startTrackingLocationService()
  }

  override fun onMapReady(googleMap: GoogleMap) {
    this.map = googleMap
  }

  override fun onLocationGrant(identifyNumber: Int) {
    loadMapFragment()
  }

  override fun onBackPressed() {
    val recordingSession = viewModel.session.value
    if (recordingSession != null && recordingSession.state == SessionState.RECORDING.name) {
      Toast.makeText(this, R.string.you_are_recording, Toast.LENGTH_SHORT).show()
      return
    } else {
      super.onBackPressed()
    }
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
}