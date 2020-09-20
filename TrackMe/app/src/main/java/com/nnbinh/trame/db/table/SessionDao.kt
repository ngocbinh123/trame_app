package com.nnbinh.trame.db.table

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nnbinh.trame.data.SessionState

@Dao
interface SessionDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun save(session: Session): Long

  @Update
  fun update(session: Session)

  @Query("SELECT * FROM Session ORDER BY id desc")
  fun getAll(): LiveData<List<Session>>

  @Query("SELECT * FROM Session ORDER BY id desc")
  fun getAllWithoutLive(): List<Session>

  /**
   * default: getting recording session
   * expect: There is 1 recording session
   * */
  @Query("SELECT * FROM Session WHERE state = :state ORDER BY id desc")
  fun getSessionByStateWithoutLive(state: String = SessionState.RECORDING.name): List<Session>

  @Query("SELECT * FROM Session WHERE id = :id LIMIT 1")
  fun getById(id: Long): LiveData<Session>

  @Query("SELECT * FROM Session WHERE id = :id LIMIT 1")
  fun getByIdWithoutLive(id: Long): Session?
}