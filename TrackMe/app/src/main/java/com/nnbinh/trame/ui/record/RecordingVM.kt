package com.nnbinh.trame.ui.record

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.nnbinh.trame.data.RecordState
import com.nnbinh.trame.data.RecordState.NEW
import com.nnbinh.trame.db.table.Session
import com.nnbinh.trame.db.table.SessionLocation
import com.nnbinh.trame.extension.addInto
import com.nnbinh.trame.extension.roundWith
import com.nnbinh.trame.extension.toSecond
import com.nnbinh.trame.repo.SessionLocationRepo
import com.nnbinh.trame.repo.SessionRepo
import com.nnbinh.trame.ui.BaseVM
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.Date

class RecordingVM(application: Application) : BaseVM(application) {
  private val TAG = "RecordingVM"
  private val sessionRepo: SessionRepo by lazy { SessionRepo(application.applicationContext) }
  private val locationRepo: SessionLocationRepo by lazy {
    SessionLocationRepo(application.applicationContext)
  }
  val sessionId: MutableLiveData<Long> = MutableLiveData<Long>().apply { value = 0 }
  val session = Transformations.switchMap(sessionId) {
    return@switchMap sessionRepo.getById(it)
  }

  val locations = Transformations.switchMap(sessionId) {
    return@switchMap locationRepo.getBySessionId(it)
  }

  private var lastLocationTime: Long = 0
  var lastLocation: MutableLiveData<Location> = MutableLiveData<Location>()
  var curDistance: MutableLiveData<Float> = MutableLiveData<Float>().apply { value = 0.0f }
  var curSpeed: MutableLiveData<Float> = MutableLiveData<Float>().apply { value = 0.0f }
  var recordingTime: MutableLiveData<Long> = MutableLiveData<Long>().apply { value = 0 }

  var recordState: MutableLiveData<RecordState> = MutableLiveData<RecordState>().apply { value = NEW }

  fun createNewSession() {
    Maybe
        .fromCallable {
          val session = Session()
          session.id = sessionRepo.save(session)

          return@fromCallable session
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ newSession ->
//          session.value = newSession
        }, { t ->
          Log.e(TAG, t.message)
        })
        .addInto(rxDispose)
  }

  fun onSaveNewLocation(location: Location) {
    Log.d(TAG, "${location.longitude} - ${location.latitude}")
    if (lastLocation.value == null) {
      lastLocationTime = Date().time
      lastLocation.value = location
      return
    }

    Maybe
        .fromCallable {
          val distance: Float = lastLocation.value?.distanceTo(location) ?: 0.0f
          val newLocationTime = Date().time
          val duration = (newLocationTime - lastLocationTime).toSecond()

          if (duration == 0L) {
            throw Throwable(
                "Ignore location: longitude=${location.longitude} - latitude=${location.latitude}")
          }

          lastLocationTime = newLocationTime
          val speed = distance / duration
          val sessionLocation = SessionLocation(
              preLongitude = lastLocation.value!!.longitude,
              prevLatitude = lastLocation.value!!.latitude,
              longitude = location.longitude,
              latitude = location.latitude,
              sessionId = session.value!!.id,
              createdAt = Date().time,
              speed = speed,
              distance = distance,
              duration = duration
          )

          locationRepo.save(sessionLocation)

          session.value?.locations?.add(sessionLocation)
          sessionRepo.update(session.value!!)
          return@fromCallable Pair(distance, speed)
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ pair ->
          Log.d(TAG, "${curDistance.value} - ${pair.first} - ${pair.second}")
          curDistance.value = curDistance.value!! + pair.first
          curSpeed.value = pair.second
          lastLocation.value = location
          updateSession()
        }, { t ->
          Log.e(TAG, t.message)
        })
        .addInto(rxDispose)
  }

  fun updateSession() {
    Maybe
        .fromCallable {
          session.value?.let { sessionValue ->
            sessionValue.distance = curDistance.value!!
            sessionValue.duration = recordingTime.value!!
            if (sessionValue.locations.isNotEmpty()) {
              val total = sessionValue.locations.sumByDouble { it.speed.toDouble() }.toFloat()
              sessionValue.avgSpeed = (total / sessionValue.locations.size)
            }
          }
          sessionRepo.update(session.value!!)
          return@fromCallable true
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
        }, { t ->
          Log.e(TAG, t.message)
        })
        .addInto(rxDispose)
  }
}