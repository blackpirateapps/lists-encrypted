package com.blackpiratex.flowye2ee.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class RichText(
    val text: String,
    val spans: List<SpanRange>
)

@Serializable
data class SpanRange(
    val start: Int,
    val end: Int,
    val style: SpanStyleType
)

@Serializable
enum class SpanStyleType {
    BOLD,
    ITALIC,
    UNDERLINE,
    STRIKETHROUGH,
    INLINE_CODE,
    LINK
}
