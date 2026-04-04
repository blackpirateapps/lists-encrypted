package com.blackpiratex.flowye2ee.domain.model

data class NodeTree(
    val node: Node,
    val children: List<NodeTree>
)
