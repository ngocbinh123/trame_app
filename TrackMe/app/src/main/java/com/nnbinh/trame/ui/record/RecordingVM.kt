package com.nnbinh.trame.ui.record

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.nnbinh.trame.data.RecordState
import com.nnbinh.trame.data.RecordState.NULL
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
  private var lastLocationTime: Long = 0
  var lastLocation: MutableLiveData<Location> = MutableLiveData<Location>()
  var curDistance: MutableLiveData<Double> = MutableLiveData<Double>().apply { value = 0.0 }
  var curSpeed: MutableLiveData<Double> = MutableLiveData<Double>().apply { value = 0.0 }
  var recordingTime: MutableLiveData<String> = MutableLiveData<String>().apply { value = "00:00:00" }
  var session: MutableLiveData<Session> = MutableLiveData<Session>()
  var recordState: MutableLiveData<RecordState> = MutableLiveData<RecordState>().apply { value = NULL }

  fun createNewSession() {
    Maybe
        .fromCallable {
          val session = Session(
              id = 0L,
              createdAt = Date().time,
              averageSpeed = 0.0,
              distance = 0.0,
              duration = "00:00:00"
          )
          session.id = sessionRepo.save(session)

          return@fromCallable session
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ newSession ->
          session.value = newSession
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
          val distance = lastLocation.value?.distanceTo(location)?.toDouble() ?: 0.0
          val newLocationTime = Date().time
          val duration = (newLocationTime - lastLocationTime).toSecond()

          if (duration == 0L) {
            throw Throwable("Ignore location: longitude=${location.longitude} - latitude=${location.latitude}")
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
              val total = sessionValue.locations.sumByDouble { it.speed }
              sessionValue.averageSpeed = (total / sessionValue.locations.size).roundWith()
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