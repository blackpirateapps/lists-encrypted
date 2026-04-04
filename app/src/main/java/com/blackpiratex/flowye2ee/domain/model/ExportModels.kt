package com.blackpiratex.flowye2ee.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ExportNode(
    val id: String,
    val parentId: String?,
    val content: RichText,
    val style: NodeStyle,
    val isCompleted: Boolean,
    val position: Int,
    val createdAt: Long,
    val updatedAt: Long
)

@Serializable
data class ExportBundle(
    val nodes: List<ExportNode>
)
