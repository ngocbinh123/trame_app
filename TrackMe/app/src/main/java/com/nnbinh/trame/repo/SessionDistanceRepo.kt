package com.nnbinh.trame.repo

import android.content.Context

/**
 * provide methods to check/save SessionDistance
 * Scope: HistoriesVM, RecordingVM
 * */
class SessionDistanceRepo(context: Context) : BaseRepo(context) {
  fun getBySessionId(sessionId: Long) = db.locationDao().getBySessionId(sessionId)
  fun getBySessionIdWithoutLive(sessionId: Long) = db.locationDao().getBySessionIdWithoutLive(
      sessionId)
}