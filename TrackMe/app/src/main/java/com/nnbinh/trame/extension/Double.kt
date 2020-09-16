package com.nnbinh.trame.extension

import com.nnbinh.trame.data.ONE_HOUR
import java.math.BigDecimal
import java.math.RoundingMode


fun Double.toKilometerFromMeter() = (this * 0.001)

fun Double.roundWith(decimalNum: Int = 4): Double {
  return if (this != Double.POSITIVE_INFINITY) {
    return BigDecimal(this).setScale(decimalNum,
        RoundingMode.HALF_EVEN).toDouble()
  }else {
    0.0
  }
}


fun Double.toHour() = (this / ONE_HOUR)