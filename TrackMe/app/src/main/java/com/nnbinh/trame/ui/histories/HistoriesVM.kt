package com.nnbinh.trame.ui.histories

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.nnbinh.trame.data.PERCENT_DEFAULT_ZOOM
import com.nnbinh.trame.data.RecordState.RECORDING
import com.nnbinh.trame.db.table.Session
import com.nnbinh.trame.extension.addInto
import com.nnbinh.trame.repo.SessionLocationRepo
import com.nnbinh.trame.repo.SessionRepo
import com.nnbinh.trame.ui.BaseVM
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class HistoriesVM(application: Application) : BaseVM(application) {
  private val TAG = "HistoriesVM"
  private val sessionRepo: SessionRepo by lazy { SessionRepo(application.applicationContext) }
  private val locationRepo: SessionLocationRepo by lazy {
    SessionLocationRepo(application.applicationContext)
  }
  val sessions: LiveData<List<Session>> = sessionRepo.getAll()
  val startSession: MutableLiveData<Session> = MutableLiveData()
  fun checkHasRecordingSession() {
    Maybe
        .fromCallable {
          return@fromCallable sessionRepo.getAllWithoutLive().firstOrNull { it.recordState == RECORDING.name }
              ?: throw Throwable("Don't have service is running in background")
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          startSession.value = it
        }, { t ->
          Log.e(TAG, t.message)
        })
        .addInto(rxDispose)
  }

  fun createNewSession() {
    Maybe
        .fromCallable {
          val session = Session()
          session.id = sessionRepo.save(session)

          return@fromCallable session
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          startSession.value = it
        }, { t ->
          Log.e(TAG, t.message)
        })
        .addInto(rxDispose)
  }

  fun loadRouteMapForSession(session: Session, map: GoogleMap) {
    Maybe
        .fromCallable {
          val locations = locationRepo.getBySessionIdWithoutLive(session.id)
          if (locations.isEmpty()) throw Throwable("session(${session.id}) does not have location")
          return@fromCallable locations
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ locations ->
          locations.forEachIndexed { index, location ->
            val place = LatLng(location.latitude, location.longitude)
            map.addMarker(MarkerOptions().position(place))

            if (locations.size - 1 == index) {
              map.moveCamera(CameraUpdateFactory.newLatLngZoom(place, PERCENT_DEFAULT_ZOOM))
            }
          }
        }, { t ->
          Log.e(TAG, t.message)
        })
        .addInto(rxDispose)
  }
}