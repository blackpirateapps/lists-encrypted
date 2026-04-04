package com.blackpiratex.flowye2ee.data.repository

import com.blackpiratex.flowye2ee.domain.model.NodeStyle
import com.blackpiratex.flowye2ee.domain.model.RichText

data class DemoNode(
    val text: String,
    val style: NodeStyle,
    val children: List<DemoNode> = emptyList()
)

object DemoOutline {
    val root = listOf(
        DemoNode(
            text = "Welcome to FlowyE2EE",
            style = NodeStyle.HEADING_1,
            children = listOf(
                DemoNode("Everything is a list item", NodeStyle.BULLET),
                DemoNode("Tab to indent, Shift+Tab to unindent", NodeStyle.BULLET),
                DemoNode("Press / for commands", NodeStyle.BULLET),
                DemoNode("Try a TODO node", NodeStyle.TODO)
            )
        ),
        DemoNode(
            text = "Formatting",
            style = NodeStyle.HEADING_2,
            children = listOf(
                DemoNode("Bold, italic, underline, code", NodeStyle.BULLET),
                DemoNode("Links are inline spans", NodeStyle.BULLET)
            )
        ),
        DemoNode(
            text = "Styles",
            style = NodeStyle.HEADING_2,
            children = listOf(
                DemoNode("Heading 3 example", NodeStyle.HEADING_3),
                DemoNode("A paragraph style node", NodeStyle.PARAGRAPH),
                DemoNode("A quoted idea", NodeStyle.QUOTE),
                DemoNode("Divider", NodeStyle.DIVIDER)
            )
        )
    )

    fun toRichText(text: String): RichText = RichText(text = text, spans = emptyList())
}
