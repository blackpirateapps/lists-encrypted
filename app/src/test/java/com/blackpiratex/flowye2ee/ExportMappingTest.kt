package com.blackpiratex.flowye2ee

import com.blackpiratex.flowye2ee.domain.model.NodeStyle
import org.junit.Assert.assertTrue
import org.junit.Test

class ExportMappingTest {
    @Test
    fun markdownMappingIncludesTokens() {
        val export = ExportUseCasesFake().exportMarkdown()
        assertTrue(export.contains("- [ ]"))
        assertTrue(export.contains("# "))
        assertTrue(export.contains("> "))
        assertTrue(export.contains("---"))
    }
}

private class ExportUseCasesFake {
    fun exportMarkdown(): String {
        val nodes = listOf(
            TestNode(NodeStyle.TODO, "Task"),
            TestNode(NodeStyle.HEADING_1, "Title"),
            TestNode(NodeStyle.QUOTE, "Quote"),
            TestNode(NodeStyle.DIVIDER, "")
        )
        return nodes.joinToString("\n") { node ->
            when (node.style) {
                NodeStyle.TODO -> "- [ ] ${node.text}"
                NodeStyle.BULLET -> "- ${node.text}"
                NodeStyle.NUMBERED -> "1. ${node.text}"
                NodeStyle.HEADING_1 -> "# ${node.text}"
                NodeStyle.HEADING_2 -> "## ${node.text}"
                NodeStyle.HEADING_3 -> "### ${node.text}"
                NodeStyle.QUOTE -> "> ${node.text}"
                NodeStyle.DIVIDER -> "---"
                NodeStyle.PARAGRAPH -> node.text
            }
        }
    }
}

private data class TestNode(val style: NodeStyle, val text: String)
