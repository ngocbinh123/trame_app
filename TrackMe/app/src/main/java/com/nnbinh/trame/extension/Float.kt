package com.nnbinh.trame.extension

import com.nnbinh.trame.data.ONE_METER
import kotlin.math.ceil

fun Float.toKilometerFromMeter() = ceil(this * ONE_METER)