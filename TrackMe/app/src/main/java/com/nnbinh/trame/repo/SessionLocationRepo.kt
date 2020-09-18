package com.nnbinh.trame.repo

import android.content.Context
import com.nnbinh.trame.db.table.SessionLocation

class SessionLocationRepo(context: Context) : BaseRepo(context) {
  fun getBySessionId(sessionId: Long) = db.locationDao().getBySessionId(sessionId)
  fun getBySessionIdWithoutLive(sessionId: Long) = db.locationDao().getBySessionIdWithoutLive(
      sessionId)
  fun save(location: SessionLocation) = db.locationDao().save(location)
}