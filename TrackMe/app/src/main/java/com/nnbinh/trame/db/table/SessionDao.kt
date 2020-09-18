package com.nnbinh.trame.db.table

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nnbinh.trame.data.RecordState

@Dao
interface SessionDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun save(session: Session): Long

  @Update
  fun update(session: Session)

//  @Query("UPDATE Session SET distance = :distance WHERE id = :id")
//  fun updateDistance(id: Long, distance: Double)
//
//  @Query("UPDATE Session SET avgSpeed = :newSpeed WHERE id = :id")
//  fun updateAvgSpeed(id: Long, newSpeed: Double)
//
//  @Query("UPDATE Session SET recordState = :state WHERE id = :id")
//  fun updateState(id: Long, state: String)

  @Query("SELECT * FROM Session ORDER BY id desc")
  fun getAll(): LiveData<List<Session>>

  @Query("SELECT * FROM Session ORDER BY id desc")
  fun getAllWithoutLive(): List<Session>

  /**
   * default: getting recording session
   * expect: There is 1 recording session
   * */
  @Query("SELECT * FROM Session WHERE recordState = :state ORDER BY id desc")
  fun getSessionByState(state: String = RecordState.RECORDING.name): List<Session>

  @Query("SELECT * FROM Session WHERE id = :id LIMIT 1")
  fun getById(id: Long): LiveData<Session>

  @Query("SELECT * FROM Session WHERE id = :id LIMIT 1")
  fun getByIdWithoutLive(id: Long): Session?
}