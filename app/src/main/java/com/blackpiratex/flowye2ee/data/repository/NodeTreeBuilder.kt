package com.blackpiratex.flowye2ee.data.repository

import com.blackpiratex.flowye2ee.domain.model.Node
import com.blackpiratex.flowye2ee.domain.model.NodeTree

object NodeTreeBuilder {
    fun build(nodes: List<Node>): List<NodeTree> {
        val grouped = nodes.groupBy { it.parentId }
        val roots = grouped[null].orEmpty().sortedBy { it.position }
        return roots.map { buildBranch(it, grouped) }
    }

    private fun buildBranch(node: Node, grouped: Map<String?, List<Node>>): NodeTree {
        val children = grouped[node.id].orEmpty().sortedBy { it.position }.map { buildBranch(it, grouped) }
        return NodeTree(node, children)
    }
}
