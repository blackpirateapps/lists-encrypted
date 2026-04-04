package com.blackpiratex.flowye2ee.presentation.state

import com.blackpiratex.flowye2ee.domain.model.NodeStyle

data class EditorState(
    val nodes: List<NodeUi> = emptyList(),
    val focusedNodeId: String? = null,
    val zoomNodeId: String? = null,
    val breadcrumb: List<String> = emptyList(),
    val isSlashMenuOpen: Boolean = false,
    val slashQuery: String = "",
    val selectedStyle: NodeStyle? = null,
    val searchQuery: String = "",
    val searchResults: List<NodeUi> = emptyList()
)
