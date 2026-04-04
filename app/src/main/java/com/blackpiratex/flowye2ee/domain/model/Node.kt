package com.blackpiratex.flowye2ee.domain.model

data class Node(
    val id: String,
    val parentId: String?,
    val content: RichText,
    val style: NodeStyle,
    val isCompleted: Boolean,
    val position: Int,
    val createdAt: Long,
    val updatedAt: Long,
    val isCollapsed: Boolean
)
