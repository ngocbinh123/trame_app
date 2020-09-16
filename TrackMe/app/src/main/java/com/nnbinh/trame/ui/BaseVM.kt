package com.nnbinh.trame.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.reactivex.disposables.CompositeDisposable

abstract class BaseVM(app: Application): AndroidViewModel(app) {
  val rxDispose = CompositeDisposable()
  override fun onCleared() {
    val rxDispose = CompositeDisposable()
    super.onCleared()
  }
}