package com.blackpiratex.flowye2ee.domain.model

enum class SlashCommand(val token: String, val style: NodeStyle) {
    TODO("/todo", NodeStyle.TODO),
    H1("/h1", NodeStyle.HEADING_1),
    H2("/h2", NodeStyle.HEADING_2),
    H3("/h3", NodeStyle.HEADING_3),
    QUOTE("/quote", NodeStyle.QUOTE),
    DIVIDER("/divider", NodeStyle.DIVIDER),
    NUMBERED("/numbered", NodeStyle.NUMBERED)
}
