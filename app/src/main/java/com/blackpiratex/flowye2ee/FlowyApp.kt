package com.blackpiratex.flowye2ee

import android.app.Application
import androidx.room.Room
import com.blackpiratex.flowye2ee.data.crypto.CryptoManager
import com.blackpiratex.flowye2ee.data.crypto.JsonSerializer
import com.blackpiratex.flowye2ee.data.crypto.KeyManager
import com.blackpiratex.flowye2ee.data.local.AppDatabase
import com.blackpiratex.flowye2ee.data.repository.NodeRepository

class FlowyApp : Application() {
    lateinit var database: AppDatabase
        private set
    lateinit var repository: NodeRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "flowy.db"
        ).fallbackToDestructiveMigration().build()
        val cryptoManager = CryptoManager("flowy_master_key")
        val serializer = JsonSerializer()
        val keyManager = KeyManager(this, cryptoManager, serializer)
        repository = NodeRepository(database.nodeDao(), cryptoManager, serializer, keyManager)
    }
}
