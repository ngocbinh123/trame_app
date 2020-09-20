package com.nnbinh.trame.extension

import android.view.View
/**
 * avoid to user clicks on the View(Button,ImageView,...) multiple time on 500L milliseconds
 * */
fun View.setOnSingleClickListener(listener: () -> Unit) {
  this.setOnClickListener(object : DebouncedOnClickListener(500L) {
    override fun onDebouncedClick(v: View) {
      listener()
    }
  })
}