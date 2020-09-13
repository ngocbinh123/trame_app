package com.nnbinh.trame

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.multidex.MultiDex
import com.facebook.stetho.Stetho
import io.reactivex.plugins.RxJavaPlugins
import java.util.Locale

class MainApp : Application() {
  override fun onCreate() {
    super.onCreate()
    if (BuildConfig.BUILD_TYPE.toLowerCase(Locale.ROOT) != "release") {
      Stetho.initializeWithDefaults(this)
    }

    RxJavaPlugins.setErrorHandler { t -> Log.e("Rxjava",t.message) }
  }

  override fun attachBaseContext(base: Context?) {
    super.attachBaseContext(base)
    MultiDex.install(this)
  }
}