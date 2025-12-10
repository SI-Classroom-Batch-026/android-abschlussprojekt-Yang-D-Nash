package com.example.yangdnashabschlussprojekt.data.local.database// In Ihrer AppDatabase.kt (z.B. in data/local/database/)

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.yangdnashabschlussprojekt.data.local.database.dao.TextHistoryDao
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.TextHistoryEntity

@Database(entities = [TextHistoryEntity::class, /* Ihre anderen Entities */], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun textHistoryDao(): TextHistoryDao

}