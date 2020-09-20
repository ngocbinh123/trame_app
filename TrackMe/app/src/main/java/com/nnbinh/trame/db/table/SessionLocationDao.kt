package com.nnbinh.trame.db.table

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SessionLocationDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun save(vararg sessionDistance: SessionDistance)

  @Query("SELECT * FROM SessionDistance WHERE sessionId = :sessionId ORDER BY createdAt")
  fun getBySessionId(sessionId: Long): LiveData<List<SessionDistance>>

  @Query("SELECT * FROM SessionDistance WHERE sessionId = :sessionId ORDER BY createdAt")
  fun getBySessionIdWithoutLive(sessionId: Long): List<SessionDistance>
}