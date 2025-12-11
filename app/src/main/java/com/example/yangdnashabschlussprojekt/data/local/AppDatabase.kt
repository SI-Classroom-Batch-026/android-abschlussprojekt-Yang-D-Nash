package com.example.yangdnashabschlussprojekt.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.yangdnashabschlussprojekt.data.local.database.dao.TextHistoryDao
import com.example.yangdnashabschlussprojekt.data.local.database.model.TextHistoryEntity

@Database(entities = [TextHistoryEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun textHistoryDao(): TextHistoryDao

}