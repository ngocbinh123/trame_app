package com.nnbinh.trame.repo

import android.content.Context
import android.location.Location
import android.util.Log
import com.nnbinh.trame.data.ONE_SECOND
import com.nnbinh.trame.data.RecordState
import com.nnbinh.trame.data.RecordState.RECORDING
import com.nnbinh.trame.db.table.Session
import com.nnbinh.trame.db.table.SessionLocation
import com.nnbinh.trame.extension.addInto
import com.nnbinh.trame.extension.toSecond
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.Date

class TrackingLocationServiceRepo(context: Context, sessionId: Long,
    initFailCallback: () -> Unit) : BaseRepo(context) {
  private val TAG = "TrackingLocationServiceRepo"

  private val composite: CompositeDisposable by lazy { CompositeDisposable() }

  var session: Session? = null
  private var lastLocation: Location? = null
  private var lastLocationTime: Long = 0

  init {
    Maybe
        .fromCallable {
          val recordingSession = if (sessionId.toInt() == -1) {
            db.sessionDao().getAllWithoutLive().firstOrNull { it.recordState == RECORDING.name }
          } else {
            db.sessionDao().getByIdWithoutLive(sessionId)
          } ?: throw Throwable("Could not find session with id = $sessionId ${sessionId.equals(-1)} - ${sessionId.toInt() == -1}")

          recordingSession.recordState = RECORDING.name
          db.sessionDao().update(recordingSession)
          db.locationDao().getBySessionId(recordingSession.id).value?.let { ls ->
            recordingSession.locations.clear()
            recordingSession.locations.addAll(ls)
          }
          return@fromCallable recordingSession
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ recordingSession ->
          session = recordingSession
        }, { t ->
          Log.e(TAG, t.message)
          initFailCallback()
        })
        .addInto(composite)
  }

  fun onCleared() {
    composite.clear()
  }

  fun saveNewLocation(newLocation: Location) {
    if (session == null) return
    Maybe
        .fromCallable {
//        save new location
          if (lastLocation == null) {
            lastLocationTime = Date().time
            lastLocation = newLocation
            return@fromCallable true
          }

          val newLocationTime = Date().time
          if (newLocationTime == lastLocationTime) {
            return@fromCallable true
          }

//        calculate distance, current speed and current duration from lastLocation to new Location
          val distance = lastLocation!!.distanceTo(newLocation)
          lastLocation = newLocation
          val duration = (newLocationTime - lastLocationTime).toSecond()
          lastLocationTime = newLocationTime
          val speed = distance / duration

          val sessionLocation = SessionLocation(
              preLongitude = lastLocation!!.longitude,
              prevLatitude = lastLocation!!.latitude,
              longitude = newLocation.longitude,
              latitude = newLocation.latitude,
              sessionId = session!!.id,
              createdAt = Date().time,
              speed = speed,
              distance = distance,
              duration = duration
          )
          db.locationDao().save(sessionLocation)

          session!!.locations.add(sessionLocation)

//        update session: distance total, avg speed
          session!!.distance += distance
//        session!!.duration += duration
          session!!.currentSpeed = speed
          if (session!!.locations.isNotEmpty()) {
            val total = session!!.locations.sumByDouble { it.speed.toDouble() }.toFloat()
            session!!.avgSpeed = (total / session!!.locations.size)
          }
          db.sessionDao().update(session!!)
          return@fromCallable true
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
        }, { t ->
          Log.e(TAG, t.message)
        })
        .addInto(composite)
  }

  fun updateSessionDuration() {
    if (session == null) return
    Maybe
        .fromCallable {
          session!!.duration += ONE_SECOND
          db.sessionDao().update(session!!)
          return@fromCallable true
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
        }, { t ->
          Log.e(TAG, t.message)
        })
        .addInto(composite)
  }

  fun updateSessionState(state: RecordState) {
    if (session == null) return
    Maybe
        .fromCallable {
          session!!.recordState = state.name
          db.sessionDao().update(session!!)
          return@fromCallable true
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
        }, { t ->
          Log.e(TAG, t.message)
        })
        .addInto(composite)
  }
}