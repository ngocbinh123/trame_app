package com.nnbinh.trame.db.table

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface SessionDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun save(session: Session): Long

  @Update
  fun update(session: Session)

  @Query("SELECT * FROM Session ORDER BY id desc")
  fun getAll(): LiveData<List<Session>>
}