package com.nnbinh.trame.helper

import android.Manifest
import android.app.Activity
import com.nnbinh.trame.R
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.lang.ref.WeakReference


/**
 * Support check and request permissions
 * */
class PermissionHelper(activity: Activity) : EasyPermissions.PermissionCallbacks {
  companion object {
    val PER_LOCATIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    val REQUEST_CODE_LOCATION = 1001
  }

  private var activityRef: WeakReference<Activity> = WeakReference(activity)
  private var identifyNumber = 0

  /**
   * check and request permissions: Location
   */
  fun performLocationTask(identifyNumber: Int = 0) {
    val context = activityRef.get() ?: return
    this.identifyNumber = identifyNumber
    if (hasPermission(*PER_LOCATIONS)) {
      doAction(REQUEST_CODE_LOCATION)
    } else {
      EasyPermissions.requestPermissions(context, context.getString(R.string.rationale_location),
          REQUEST_CODE_LOCATION, *PER_LOCATIONS)
    }
  }

  /**
   *  callback
   */
  override fun onRequestPermissionsResult(
      requestCode: Int,
      permissions: Array<out String>,
      grantResults: IntArray
  ) {
    EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
  }

  override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    doAction(requestCode)
  }

  override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
    val context = activityRef.get() ?: return
    if (!EasyPermissions.somePermissionPermanentlyDenied(context, perms)) {
      doAction(requestCode)
      return
    }

    AppSettingsDialog.Builder(activityRef.get()!!)
        .setRequestCode(requestCode)
        .build().show()
  }

  /**
   *  when use change permission in device Setting, this fun should be call
   */
  fun onActivityResult(requestCode: Int) {
    doAction(requestCode)
  }

  private fun hasPermission(vararg permissions: String): Boolean {
    return if (activityRef.get() == null) {
      false
    } else {
      EasyPermissions.hasPermissions(activityRef.get()!!, *permissions)
    }
  }

  private fun doAction(requestCode: Int) {
    val context = activityRef.get() ?: return
    if (requestCode == REQUEST_CODE_LOCATION && context is LocationPermissionCallBack) {
      if (hasPermission(*PER_LOCATIONS)) context.onLocationGrant(identifyNumber)
      else context.onLocationDeny(identifyNumber)
    }
  }

  interface LocationPermissionCallBack {
    fun onLocationGrant(identifyNumber : Int)
    fun onLocationDeny(identifyNumber : Int)
  }
}