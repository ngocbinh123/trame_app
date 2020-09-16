package com.nnbinh.trame.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.nnbinh.trame.BuildConfig
import com.nnbinh.trame.data.PERCENT_DEFAULT_ZOOM
import com.nnbinh.trame.helper.PermissionHelper

abstract class BaseActivity: AppCompatActivity(), OnMapReadyCallback, PermissionHelper.LocationPermissionCallBack {
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
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(10.8010969, 106.6493749)
      googleMap.addMarker(MarkerOptions().position(sydney).title("Scetpa Building"))
      googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, PERCENT_DEFAULT_ZOOM))
    }

    override fun onLocationGrant(identifyNumber: Int) {
    }

    override fun onLocationDeny(identifyNumber: Int) {
        this.finish()
    }

  private fun openSettings() {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    val uri: Uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
    intent.data = uri
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    startActivity(intent)
  }
}