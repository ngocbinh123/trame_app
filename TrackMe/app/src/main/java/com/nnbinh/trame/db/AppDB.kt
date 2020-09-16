package com.nnbinh.trame.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nnbinh.trame.db.table.SessionLocation
import com.nnbinh.trame.db.table.SessionLocationDao
import com.nnbinh.trame.db.table.Session
import com.nnbinh.trame.db.table.SessionDao

@Database(
    entities = [Session::class, SessionLocation::class],
    version = 1
)
abstract class AppDB : RoomDatabase() {
  companion object {
    @Volatile
    private var INSTANCE: AppDB? = null
    fun getInstance(context: Context): AppDB {
      synchronized(AppDB::class) {
        if (INSTANCE == null) {
          INSTANCE = Room.databaseBuilder(context, AppDB::class.java, "trame.db")
              .fallbackToDestructiveMigration()
              .build()
        }
      }
      return INSTANCE!!
    }
  }
  abstract fun sessionDao(): SessionDao
  abstract fun locationDao(): SessionLocationDao
}