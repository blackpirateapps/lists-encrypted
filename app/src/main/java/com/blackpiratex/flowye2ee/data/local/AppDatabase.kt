package com.blackpiratex.flowye2ee.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.blackpiratex.flowye2ee.data.local.dao.NodeDao
import com.blackpiratex.flowye2ee.data.local.entity.NodeEntity

@Database(
    entities = [NodeEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun nodeDao(): NodeDao
}
