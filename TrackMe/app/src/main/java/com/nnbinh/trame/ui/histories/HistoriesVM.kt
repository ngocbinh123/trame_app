package com.nnbinh.trame.ui.histories

import android.app.Application
import androidx.lifecycle.LiveData
import com.nnbinh.trame.db.table.Session
import com.nnbinh.trame.repo.SessionRepo
import com.nnbinh.trame.ui.BaseVM

class HistoriesVM(application: Application) : BaseVM(application) {
  private val TAG = "HistoriesVM"
  private val sessionRepo: SessionRepo by lazy { SessionRepo(application.applicationContext) }
  val sessions: LiveData<List<Session>> = sessionRepo.getAll()
}