package com.blackpiratex.flowye2ee.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.blackpiratex.flowye2ee.data.local.dao.NodeDao
import com.blackpiratex.flowye2ee.data.local.entity.NodeEntity

@Database(
    entities = [NodeEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun nodeDao(): NodeDao
}
