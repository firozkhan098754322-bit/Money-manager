package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.SplitExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SplitExpenseDao {
    @Query("SELECT * FROM split_expenses ORDER BY date DESC, id DESC")
    fun getAllSplits(): Flow<List<SplitExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSplit(split: SplitExpenseEntity): Long

    @Update
    suspend fun updateSplit(split: SplitExpenseEntity)

    @Delete
    suspend fun deleteSplit(split: SplitExpenseEntity)

    @Query("DELETE FROM split_expenses WHERE id = :id")
    suspend fun deleteSplitById(id: Long)
}
