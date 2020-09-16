package com.nnbinh.trame.extension

import com.nnbinh.trame.data.ONE_SECOND


/**
 * Convert to millisecond to Second
 * 1s = 1000ms
 * */
fun Long.toSecond() = this / ONE_SECOND