package com.nnbinh.trame.ui.histories

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.nnbinh.trame.data.SessionState.RECORDING
import com.nnbinh.trame.db.table.Session
import com.nnbinh.trame.extension.addInto
import com.nnbinh.trame.helper.MapHelper
import com.nnbinh.trame.repo.SessionDistanceRepo
import com.nnbinh.trame.repo.SessionRepo
import com.nnbinh.trame.ui.BaseVM
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class HistoriesVM(application: Application) : BaseVM(application) {
  private val TAG = "HistoriesVM"
  private val sessionRepo: SessionRepo by lazy { SessionRepo(application.applicationContext) }
  private val mDistanceRepo: SessionDistanceRepo by lazy {
    SessionDistanceRepo(application.applicationContext)
  }
  val sessions: LiveData<List<Session>> = sessionRepo.getAll()
  val startSession: MutableLiveData<Session> = MutableLiveData()
  fun checkHasRecordingSession() {
    Maybe
        .fromCallable {
          return@fromCallable sessionRepo.getAllWithoutLive().firstOrNull { it.state == RECORDING.name }
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
          val locations = mDistanceRepo.getBySessionIdWithoutLive(session.id)
          if (locations.isEmpty()) throw Throwable("session(${session.id}) does not have location")

          val paths: MutableList<LatLng> = locations.map {
            LatLng(it.latitude, it.longitude)
          }.toMutableList()

          paths.add(0, LatLng(locations.first().prevLatitude, locations.first().prevLatitude))
          return@fromCallable paths
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ paths ->
          val helper = MapHelper(map)
          helper.addStartEndPlaces(paths.first(), paths.last())
          helper.drawFullRoute(paths)
          helper.moveTo(paths.last())
        }, { t ->
          Log.e(TAG, t.message)
        })
        .addInto(rxDispose)
  }
}