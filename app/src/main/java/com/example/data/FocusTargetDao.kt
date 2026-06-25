package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusTargetDao {
    @Query("SELECT * FROM focus_targets ORDER BY targetDate ASC")
    fun getAllTargets(): Flow<List<FocusTarget>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTarget(target: FocusTarget)

    @Update
    suspend fun updateTarget(target: FocusTarget)

    @Delete
    suspend fun deleteTarget(target: FocusTarget)

    @Query("DELETE FROM focus_targets WHERE id = :id")
    suspend fun deleteTargetById(id: Int)
}
