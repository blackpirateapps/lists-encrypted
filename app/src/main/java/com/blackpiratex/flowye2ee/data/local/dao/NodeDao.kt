package com.blackpiratex.flowye2ee.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.blackpiratex.flowye2ee.data.local.entity.NodeEntity

@Dao
interface NodeDao {
    @Query("SELECT * FROM NodeEntity WHERE parentId IS NULL ORDER BY position ASC")
    suspend fun getRootNodes(): List<NodeEntity>

    @Query("SELECT * FROM NodeEntity WHERE parentId = :parentId ORDER BY position ASC")
    suspend fun getChildren(parentId: String): List<NodeEntity>

    @Query("SELECT * FROM NodeEntity WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): NodeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(node: NodeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(nodes: List<NodeEntity>)

    @Update
    suspend fun update(node: NodeEntity)

    @Query("DELETE FROM NodeEntity WHERE id = :id")
    suspend fun deleteById(id: String)
}
