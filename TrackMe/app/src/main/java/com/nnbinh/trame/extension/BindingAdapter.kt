package com.nnbinh.trame.extension

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.nnbinh.trame.data.ONE_HOUR
import com.nnbinh.trame.data.ONE_MINUTE
import com.nnbinh.trame.data.ONE_SECOND
import com.nnbinh.trame.data.SPEED_OF_KM_HOUR

@BindingAdapter("distance")
fun showInstance(v: TextView, distance: Float? = null) {
  v.text = if (distance == null || distance == 0.0f) {
    "0.0 km"
  } else {
    String.format("%1$,.2f km", distance.toKilometerFromMeter())
  }
}

@BindingAdapter("speed")
fun showSpeed(v: TextView, speed: Float? = null) {
  v.text = if (speed == null || speed == 0.0f) {
    "0.0 km/h"
  } else {
    String.format("%1$,.2f km/h", speed * SPEED_OF_KM_HOUR)
  }
}

@BindingAdapter("duration")
fun showDuration(v: TextView, duration: Long? = null) {
  if (duration == null || duration == 0L) {
    v.text = "00:00:00"
    return
  }

  val hour = duration / ONE_HOUR
  var remainSecond = duration % ONE_HOUR
  val minute = remainSecond / ONE_MINUTE
  remainSecond = (remainSecond % ONE_MINUTE) / ONE_SECOND
  v.text = String.format("%02d:%02d:%02d", hour, minute, remainSecond)
}