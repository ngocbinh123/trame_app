package com.nnbinh.trame.extension

import android.view.View

fun View.setOnSingleClickListener(listener: () -> Unit) {
  this.setOnClickListener(object : DebouncedOnClickListener(500L) {
    override fun onDebouncedClick(v: View) {
      listener()
    }
  })
}