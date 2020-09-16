package com.nnbinh.trame.repo

import android.content.Context
import com.nnbinh.trame.db.AppDB

abstract class BaseRepo(context: Context) {
  protected val db: AppDB by lazy { AppDB.getInstance(context) }
}