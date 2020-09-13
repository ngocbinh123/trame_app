package com.nnbinh.trame.extension

import android.os.SystemClock
import android.view.View
import java.util.WeakHashMap
import kotlin.math.abs

/**
 * A Debounced OnClickListener
 * Rejects clicks that are too close together in timeInMillis.
 * This class is safe to use as an OnClickListener for multiple views, and will debounce each one separately.
 * @param minimumIntervalMsec The minimum allowed timeInMillis between clicks - any click sooner than this after a previous click will be rejected
 */
abstract class DebouncedOnClickListener(private val minimumInterval: Long) : View.OnClickListener {
  private val lastClickMap: MutableMap<View, Long> = WeakHashMap()

  /**
   * Implement this in your subclass instead of onClick
   * @param v The view that was clicked
   */
  abstract fun onDebouncedClick(v: View)

  override fun onClick(clickedView: View) {
    val previousClickTimestamp = lastClickMap[clickedView]
    val currentTimestamp = SystemClock.uptimeMillis()

    lastClickMap[clickedView] = currentTimestamp
    if (previousClickTimestamp == null || abs(
            currentTimestamp - previousClickTimestamp.toLong()) > minimumInterval) {
      onDebouncedClick(clickedView)
    }
  }
}