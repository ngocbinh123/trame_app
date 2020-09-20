package com.nnbinh.trame.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.nnbinh.trame.helper.PermissionHelper

abstract class BaseActivity : AppCompatActivity(), PermissionHelper.LocationPermissionCallBack {
  val REQ_ACCESS_LOCATION = 2001
  val permissionHelper: PermissionHelper by lazy { PermissionHelper(this) }

  override fun onResume() {
    super.onResume()
    permissionHelper.performLocationTask(REQ_ACCESS_LOCATION)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    permissionHelper.onActivityResult(requestCode)
  }

  override fun onLocationGrant(identifyNumber: Int) {
  }

  override fun onLocationDeny(identifyNumber: Int) {
    this.finish()
  }
}