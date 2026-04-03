package com.blackpiratex.flowye2ee.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.blackpiratex.flowye2ee.domain.model.NodeStyle

@Entity
data class NodeEntity(
    @PrimaryKey val id: String,
    val parentId: String?,
    val encryptedContent: ByteArray,
    val style: NodeStyle,
    val isCompleted: Boolean,
    val position: Int,
    val createdAt: Long,
    val updatedAt: Long
)
