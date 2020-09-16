package com.nnbinh.trame.repo

import android.content.Context
import com.nnbinh.trame.db.table.SessionLocation

class SessionLocationRepo(context: Context) : BaseRepo(context) {
  fun save(location: SessionLocation) = db.locationDao().save(location)
}