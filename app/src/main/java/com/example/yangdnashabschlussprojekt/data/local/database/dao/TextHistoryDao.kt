package com.example.yangdnashabschlussprojekt.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.TextHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TextHistoryDao {
    @Insert
    suspend fun insert(item: TextHistoryEntity)
    @Query("SELECT * FROM text_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<TextHistoryEntity>>
    @Query("DELETE FROM text_history WHERE id = :itemId")
    suspend fun deleteById(itemId: Long)
    @Query("DELETE FROM text_history")
    suspend fun deleteAll()
}