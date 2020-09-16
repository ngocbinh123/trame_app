package com.nnbinh.trame.extension

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.nnbinh.trame.data.SPEED_OF_KM_HOUR

@BindingAdapter("distance")
fun showInstance(v: TextView, distance: Double? = null) {
  if (distance == null || distance == 0.0 || distance == Double.POSITIVE_INFINITY) {
    v.text = "0.0 km"
  } else {
    v.text = String.format("%1$,.2f km", distance.toKilometerFromMeter())
  }
}

@BindingAdapter("speed")
fun showSpeed(v: TextView, speed: Double? = null) {
  if (speed == null || speed == 0.0 || speed == Double.POSITIVE_INFINITY) {
    v.text = "0.0 km/h"
  } else {
    v.text = String.format("%1$,.2f km/h", speed * SPEED_OF_KM_HOUR)
  }
}