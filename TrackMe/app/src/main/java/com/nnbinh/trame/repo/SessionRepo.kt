package com.nnbinh.trame.repo

import android.content.Context
import com.nnbinh.trame.db.table.Session

/**
 * provide methods to check/save Session
 * Scope: HistoriesVM, RecordingVM
 * */
class SessionRepo(context: Context) : BaseRepo(context) {
  fun getAll() = db.sessionDao().getAll()
  fun getAllWithoutLive() = db.sessionDao().getAllWithoutLive()
  fun getById(id: Long) = db.sessionDao().getById(id)
  fun save(session: Session) = db.sessionDao().save(session)
}