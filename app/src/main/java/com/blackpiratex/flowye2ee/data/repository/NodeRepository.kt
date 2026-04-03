package com.blackpiratex.flowye2ee.data.repository

import com.blackpiratex.flowye2ee.data.crypto.CryptoManager
import com.blackpiratex.flowye2ee.data.crypto.JsonSerializer
import com.blackpiratex.flowye2ee.data.local.dao.NodeDao
import com.blackpiratex.flowye2ee.data.local.entity.NodeEntity
import com.blackpiratex.flowye2ee.domain.model.NodeStyle
import com.blackpiratex.flowye2ee.domain.model.RichText
import java.util.UUID

class NodeRepository(
    private val nodeDao: NodeDao,
    private val cryptoManager: CryptoManager,
    private val serializer: JsonSerializer
) {
    suspend fun createNode(
        parentId: String?,
        text: RichText,
        style: NodeStyle,
        position: Int
    ): NodeEntity {
        val now = System.currentTimeMillis()
        val encrypted = cryptoManager.encrypt(serializer.serialize(text).encodeToByteArray())
        val node = NodeEntity(
            id = UUID.randomUUID().toString(),
            parentId = parentId,
            encryptedContent = encrypted,
            style = style,
            isCompleted = false,
            position = position,
            createdAt = now,
            updatedAt = now
        )
        nodeDao.upsert(node)
        return node
    }

    suspend fun getNodeContent(node: NodeEntity): RichText {
        val decrypted = cryptoManager.decrypt(node.encryptedContent)
        val decoded = decrypted.decodeToString()
        return serializer.deserialize(decoded)
    }

    suspend fun updateNodeContent(node: NodeEntity, richText: RichText): NodeEntity {
        val encrypted = cryptoManager.encrypt(serializer.serialize(richText).encodeToByteArray())
        val updated = node.copy(encryptedContent = encrypted, updatedAt = System.currentTimeMillis())
        nodeDao.upsert(updated)
        return updated
    }
}
