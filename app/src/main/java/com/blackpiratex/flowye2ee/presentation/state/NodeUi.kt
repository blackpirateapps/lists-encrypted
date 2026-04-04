package com.blackpiratex.flowye2ee.presentation.state

import com.blackpiratex.flowye2ee.domain.model.NodeStyle
import com.blackpiratex.flowye2ee.domain.model.RichText

data class NodeUi(
    val id: String,
    val parentId: String?,
    val content: RichText,
    val style: NodeStyle,
    val isCompleted: Boolean,
    val position: Int,
    val isCollapsed: Boolean,
    val depth: Int,
    val hasChildren: Boolean
)
