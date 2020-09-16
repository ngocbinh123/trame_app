package com.nnbinh.trame.extension

import kotlin.math.ceil
import kotlin.math.round

fun Float.toKilometerFromMeter() = ceil(this * 0.001f)