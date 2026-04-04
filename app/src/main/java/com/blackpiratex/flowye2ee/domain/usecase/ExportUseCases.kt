package com.blackpiratex.flowye2ee.domain.usecase

import com.blackpiratex.flowye2ee.data.repository.NodeRepository
import com.blackpiratex.flowye2ee.domain.model.NodeStyle
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ExportUseCases(private val repository: NodeRepository) {
    private val json = Json { prettyPrint = true }

    suspend fun exportJson(): String {
        val bundle = repository.exportJson()
        return json.encodeToString(bundle)
    }

    suspend fun exportMarkdown(): String {
        val nodes = repository.loadAllDomainNodes()
        val grouped = nodes.groupBy { it.parentId }
        val builder = StringBuilder()
        fun walk(parentId: String?, depth: Int) {
            grouped[parentId].orEmpty().sortedBy { it.position }.forEach { node ->
                val prefix = when (node.style) {
                    NodeStyle.TODO -> "- [ ] "
                    NodeStyle.BULLET -> "- "
                    NodeStyle.NUMBERED -> "1. "
                    NodeStyle.HEADING_1 -> "# "
                    NodeStyle.HEADING_2 -> "## "
                    NodeStyle.HEADING_3 -> "### "
                    NodeStyle.QUOTE -> "> "
                    NodeStyle.DIVIDER -> "---"
                    NodeStyle.PARAGRAPH -> ""
                }
                val indent = if (node.style == NodeStyle.HEADING_1 || node.style == NodeStyle.HEADING_2 || node.style == NodeStyle.HEADING_3) "" else "  ".repeat(depth)
                if (node.style == NodeStyle.DIVIDER) {
                    builder.append(prefix).append("\n")
                } else {
                    builder.append(indent).append(prefix).append(node.content.text).append("\n")
                }
                walk(node.id, depth + 1)
            }
        }
        walk(null, 0)
        return builder.toString()
    }
}
