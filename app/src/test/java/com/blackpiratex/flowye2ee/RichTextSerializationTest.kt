package com.blackpiratex.flowye2ee

import com.blackpiratex.flowye2ee.data.crypto.JsonSerializer
import com.blackpiratex.flowye2ee.domain.model.RichText
import com.blackpiratex.flowye2ee.domain.model.SpanRange
import com.blackpiratex.flowye2ee.domain.model.SpanStyleType
import org.junit.Assert.assertEquals
import org.junit.Test

class RichTextSerializationTest {
    @Test
    fun roundTripSerialization() {
        val serializer = JsonSerializer()
        val richText = RichText(
            text = "Hello",
            spans = listOf(
                SpanRange(0, 5, SpanStyleType.BOLD)
            )
        )
        val encoded = serializer.serialize(richText)
        val decoded = serializer.deserialize(encoded)
        assertEquals(richText, decoded)
    }
}
