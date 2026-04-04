package com.blackpiratex.flowye2ee.presentation.ui.editor

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.graphics.Color
import com.blackpiratex.flowye2ee.domain.model.RichText
import com.blackpiratex.flowye2ee.domain.model.SpanStyleType

@Composable
fun renderRichText(richText: RichText): AnnotatedString {
    return buildAnnotatedString {
        append(richText.text)
        richText.spans.forEach { span ->
            val style = when (span.style) {
                SpanStyleType.BOLD -> SpanStyle(fontWeight = FontWeight.Bold)
                SpanStyleType.ITALIC -> SpanStyle(fontStyle = FontStyle.Italic)
                SpanStyleType.UNDERLINE -> SpanStyle(textDecoration = TextDecoration.Underline)
                SpanStyleType.STRIKETHROUGH -> SpanStyle(textDecoration = TextDecoration.LineThrough)
                SpanStyleType.INLINE_CODE -> SpanStyle(fontFamily = FontFamily.Monospace, background = Color(0xFFEAEAEA))
                SpanStyleType.LINK -> SpanStyle(color = Color(0xFF1B6DF2), textDecoration = TextDecoration.Underline)
            }
            addStyle(style, span.start, span.end)
        }
    }
}
