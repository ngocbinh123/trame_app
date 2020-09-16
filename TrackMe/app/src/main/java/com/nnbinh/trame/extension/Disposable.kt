package com.nnbinh.trame.extension

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

fun Disposable.addInto(composite: CompositeDisposable) {
  composite.add(this)
}