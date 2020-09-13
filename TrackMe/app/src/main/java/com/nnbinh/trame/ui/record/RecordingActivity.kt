package com.nnbinh.trame.ui.record

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.SupportMapFragment
import com.nnbinh.trame.R
import com.nnbinh.trame.databinding.ActivityRecordingBinding
import com.nnbinh.trame.extension.setOnSingleClickListener
import com.nnbinh.trame.ui.BaseActivity

class RecordingActivity: BaseActivity() {
  companion object {
    fun getIntent(context: Context): Intent {
      return with(Intent(context, RecordingActivity::class.java)) {
        this.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        this
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding = DataBindingUtil.setContentView<ActivityRecordingBinding>(this, R.layout.activity_recording)
    binding.lifecycleOwner = this
    binding.imvPause.setOnSingleClickListener {
      binding.imvPause.visibility = View.INVISIBLE
      binding.imvRestart.visibility = View.VISIBLE
      binding.imvStop.visibility = View.VISIBLE
    }

    binding.imvRestart.setOnSingleClickListener {
      binding.imvPause.visibility = View.VISIBLE
      binding.imvRestart.visibility = View.INVISIBLE
      binding.imvStop.visibility = View.INVISIBLE
    }

    binding.imvStop.setOnSingleClickListener {
      this.finish()
    }
  }

  override fun onLocationGrant(identifyNumber: Int) {
    loadMapFragment()
  }

  private fun loadMapFragment() {
    val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
    mapFragment.getMapAsync(this)
  }
}