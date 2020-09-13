package com.nnbinh.trame.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nnbinh.trame.db.table.Session

@Database(
    entities = [Session::class],
    version = 1
)
abstract class AppDB : RoomDatabase() {

}