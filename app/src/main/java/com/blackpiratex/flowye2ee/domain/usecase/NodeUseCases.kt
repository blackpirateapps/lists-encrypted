package com.blackpiratex.flowye2ee.domain.usecase

import com.blackpiratex.flowye2ee.data.local.entity.NodeEntity
import com.blackpiratex.flowye2ee.data.repository.NodeRepository
import com.blackpiratex.flowye2ee.domain.model.NodeStyle
import com.blackpiratex.flowye2ee.domain.model.RichText

class NodeUseCases(private val repository: NodeRepository) {
    suspend fun createNode(parentId: String?, text: RichText, style: NodeStyle, position: Int): NodeEntity {
        return repository.createNode(parentId, text, style, position)
    }

    suspend fun updateContent(node: NodeEntity, content: RichText): NodeEntity {
        return repository.updateNodeContent(node, content)
    }

    suspend fun toggleTodo(node: NodeEntity): NodeEntity = repository.toggleTodo(node)

    suspend fun toggleCollapse(node: NodeEntity): NodeEntity = repository.toggleCollapse(node)

    suspend fun updateStyle(node: NodeEntity, style: NodeStyle): NodeEntity = repository.updateStyle(node, style)

    suspend fun delete(node: NodeEntity) = repository.deleteNode(node)

    suspend fun exportJson() = repository.exportJson()

    suspend fun seedDemoIfEmpty() = repository.seedDemoIfEmpty()

    suspend fun deleteSubtree(nodeId: String) = repository.deleteSubtree(nodeId)

    suspend fun indent(node: NodeEntity) = repository.indent(node)

    suspend fun unindent(node: NodeEntity) = repository.unindent(node)
}
