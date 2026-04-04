package com.blackpiratex.flowye2ee.data.repository

import com.blackpiratex.flowye2ee.data.crypto.CryptoManager
import com.blackpiratex.flowye2ee.data.crypto.JsonSerializer
import com.blackpiratex.flowye2ee.data.crypto.KeyManager
import com.blackpiratex.flowye2ee.data.local.dao.NodeDao
import com.blackpiratex.flowye2ee.data.local.entity.NodeEntity
import com.blackpiratex.flowye2ee.domain.model.ExportBundle
import com.blackpiratex.flowye2ee.domain.model.ExportNode
import com.blackpiratex.flowye2ee.domain.model.Node
import com.blackpiratex.flowye2ee.domain.model.NodeStyle
import com.blackpiratex.flowye2ee.domain.model.RichText
import java.util.UUID
import javax.crypto.SecretKey

class NodeRepository(
    private val nodeDao: NodeDao,
    private val cryptoManager: CryptoManager,
    private val serializer: JsonSerializer,
    private val keyManager: KeyManager
) {
    private suspend fun activeKey(): SecretKey {
        return keyManager.getActiveKey()
    }

    private suspend fun encrypt(text: RichText): String {
        val payload = cryptoManager.encrypt(serializer.serialize(text).encodeToByteArray(), activeKey())
        return serializer.encodePayload(payload)
    }

    private suspend fun decrypt(cipherText: String): RichText {
        val payload = serializer.decodePayload(cipherText)
        val raw = cryptoManager.decrypt(payload, activeKey())
        return serializer.deserialize(raw.decodeToString())
    }

    suspend fun createNode(
        parentId: String?,
        text: RichText,
        style: NodeStyle,
        position: Int
    ): NodeEntity {
        val now = System.currentTimeMillis()
        val encrypted = encrypt(text)
        val node = NodeEntity(
            id = UUID.randomUUID().toString(),
            parentId = parentId,
            encryptedContent = encrypted,
            style = style,
            isCompleted = false,
            position = position,
            createdAt = now,
            updatedAt = now,
            isCollapsed = false
        )
        nodeDao.upsert(node)
        return node
    }

    suspend fun getNodeContent(node: NodeEntity): RichText {
        return decrypt(node.encryptedContent)
    }

    suspend fun updateNodeContent(node: NodeEntity, richText: RichText): NodeEntity {
        val encrypted = encrypt(richText)
        val updated = node.copy(encryptedContent = encrypted, updatedAt = System.currentTimeMillis())
        nodeDao.upsert(updated)
        return updated
    }

    suspend fun updateNode(node: NodeEntity): NodeEntity {
        val updated = node.copy(updatedAt = System.currentTimeMillis())
        nodeDao.upsert(updated)
        return updated
    }

    suspend fun loadAllNodes(): List<NodeEntity> = nodeDao.getAllNodes()

    suspend fun loadAllDomainNodes(): List<Node> {
        val entities = nodeDao.getAllNodes()
        return entities.map { entity ->
            val content = decrypt(entity.encryptedContent)
            NodeMapper.toDomain(entity, content)
        }
    }

    suspend fun exportJson(): ExportBundle {
        val nodes = loadAllDomainNodes().map { node ->
            ExportNode(
                id = node.id,
                parentId = node.parentId,
                content = node.content,
                style = node.style,
                isCompleted = node.isCompleted,
                position = node.position,
                createdAt = node.createdAt,
                updatedAt = node.updatedAt
            )
        }
        return ExportBundle(nodes = nodes)
    }

    suspend fun toggleCollapse(node: NodeEntity): NodeEntity {
        val updated = node.copy(isCollapsed = !node.isCollapsed, updatedAt = System.currentTimeMillis())
        nodeDao.upsert(updated)
        return updated
    }

    suspend fun toggleTodo(node: NodeEntity): NodeEntity {
        val updated = node.copy(isCompleted = !node.isCompleted, updatedAt = System.currentTimeMillis())
        nodeDao.upsert(updated)
        return updated
    }

    suspend fun updateStyle(node: NodeEntity, style: NodeStyle): NodeEntity {
        val updated = node.copy(style = style, updatedAt = System.currentTimeMillis())
        nodeDao.upsert(updated)
        return updated
    }

    suspend fun indent(node: NodeEntity): NodeEntity? {
        val siblings = nodeDao.getChildren(node.parentId ?: return null)
        val index = siblings.indexOfFirst { it.id == node.id }
        if (index <= 0) return null
        val newParent = siblings[index - 1]
        val shifted = siblings.filter { it.position > node.position }
        shifted.forEach { sibling ->
            nodeDao.upsert(sibling.copy(position = sibling.position - 1))
        }
        val lastChildPosition = nodeDao.getChildren(newParent.id).maxOfOrNull { it.position } ?: -1
        val updated = node.copy(parentId = newParent.id, position = lastChildPosition + 1)
        nodeDao.upsert(updated)
        return updated
    }

    suspend fun unindent(node: NodeEntity): NodeEntity? {
        val parentId = node.parentId ?: return null
        val parent = nodeDao.getById(parentId) ?: return null
        val newParentId = parent.parentId
        val siblings = if (newParentId == null) nodeDao.getRootNodes() else nodeDao.getChildren(newParentId)
        val parentPosition = parent.position
        val shifted = siblings.filter { it.position > parentPosition }.sortedBy { it.position }
        shifted.forEach { sibling ->
            nodeDao.upsert(sibling.copy(position = sibling.position + 1))
        }
        val updated = node.copy(parentId = newParentId, position = parentPosition + 1)
        nodeDao.upsert(updated)
        return updated
    }

    suspend fun seedDemoIfEmpty() {
        if (nodeDao.countNodes() > 0) return
        var position = 0
        DemoOutline.root.forEach { demo ->
            val rootNode = createNode(null, DemoOutline.toRichText(demo.text), demo.style, position++)
            seedChildren(rootNode.id, demo.children)
        }
    }

    private suspend fun seedChildren(parentId: String, children: List<DemoNode>) {
        var position = 0
        children.forEach { child ->
            val node = createNode(parentId, DemoOutline.toRichText(child.text), child.style, position++)
            seedChildren(node.id, child.children)
        }
    }

    suspend fun deleteNode(node: NodeEntity) {
        nodeDao.deleteById(node.id)
    }

    suspend fun deleteSubtree(rootId: String) {
        val all = nodeDao.getAllNodes()
        val toDelete = mutableSetOf<String>()
        fun collect(id: String) {
            toDelete.add(id)
            all.filter { it.parentId == id }.forEach { child -> collect(child.id) }
        }
        collect(rootId)
        nodeDao.deleteByIds(toDelete.toList())
    }

    suspend fun reEncryptAll(oldPassword: CharArray, newPassword: CharArray) {
        val oldKey = if (oldPassword.isNotEmpty()) {
            keyManager.setPassword(oldPassword)
        } else {
            keyManager.getActiveKey()
        }
        val nodes = nodeDao.getAllNodes()
        keyManager.changePassword(newPassword)
        val newKey = keyManager.getActiveKey()
        val updated = nodes.map { node ->
            val payload = serializer.decodePayload(node.encryptedContent)
            val raw = cryptoManager.decrypt(payload, oldKey)
            val text = serializer.deserialize(raw.decodeToString())
            val newPayload = cryptoManager.encrypt(serializer.serialize(text).encodeToByteArray(), newKey)
            node.copy(encryptedContent = serializer.encodePayload(newPayload), updatedAt = System.currentTimeMillis())
        }
        nodeDao.upsertAll(updated)
    }
}
