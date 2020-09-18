package com.nnbinh.trame.db.table

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SessionLocationDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun save(vararg sessionLocation: SessionLocation)

  @Query("SELECT * FROM SessionLocation WHERE sessionId = :sessionId ORDER BY createdAt")
  fun getBySessionId(sessionId: Long): LiveData<List<SessionLocation>>

  @Query("SELECT * FROM SessionLocation WHERE sessionId = :sessionId ORDER BY createdAt")
  fun getBySessionIdWithoutLive(sessionId: Long): List<SessionLocation>
}