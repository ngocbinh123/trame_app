package com.nnbinh.trame.ui.record

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.android.gms.maps.GoogleMap
import com.nnbinh.trame.db.table.SessionDistance
import com.nnbinh.trame.extension.addInto
import com.nnbinh.trame.helper.MapHelper
import com.nnbinh.trame.repo.SessionDistanceRepo
import com.nnbinh.trame.repo.SessionRepo
import com.nnbinh.trame.ui.BaseVM
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RecordingVM(application: Application) : BaseVM(application) {
  private val sessionRepo: SessionRepo by lazy { SessionRepo(application.applicationContext) }
  private val mDistanceRepo: SessionDistanceRepo by lazy {
    SessionDistanceRepo(application.applicationContext)
  }
  val sessionId: MutableLiveData<Long> = MutableLiveData<Long>().apply { value = 0 }
  val session = Transformations.switchMap(sessionId) {
    return@switchMap sessionRepo.getById(it)
  }

  val distances = Transformations.switchMap(sessionId) {
    return@switchMap mDistanceRepo.getBySessionId(it)
  }

  fun drawRoute(map: GoogleMap) {
    val ls = distances.value ?: return
    Maybe
        .fromCallable {
          return@fromCallable SessionDistance.convertToLatLngList(ls)
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { paths ->
          map.clear()
          val helper = MapHelper(map)
          helper.addStartEndPlaces(paths.first(), paths.last())
          helper.drawFullRoute(paths)
          helper.moveTo(paths.last())
        }.addInto(rxDispose)
  }
}