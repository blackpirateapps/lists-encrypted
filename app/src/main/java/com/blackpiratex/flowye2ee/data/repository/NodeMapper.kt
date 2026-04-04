package com.blackpiratex.flowye2ee.data.repository

import com.blackpiratex.flowye2ee.data.local.entity.NodeEntity
import com.blackpiratex.flowye2ee.domain.model.Node
import com.blackpiratex.flowye2ee.domain.model.RichText

object NodeMapper {
    fun toDomain(entity: NodeEntity, content: RichText): Node {
        return Node(
            id = entity.id,
            parentId = entity.parentId,
            content = content,
            style = entity.style,
            isCompleted = entity.isCompleted,
            position = entity.position,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            isCollapsed = entity.isCollapsed
        )
    }
}
