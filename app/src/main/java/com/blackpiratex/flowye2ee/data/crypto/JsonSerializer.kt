package com.blackpiratex.flowye2ee.data.crypto

import com.blackpiratex.flowye2ee.domain.model.RichText
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class JsonSerializer {
    private val json = Json { ignoreUnknownKeys = true }

    fun serialize(richText: RichText): String = json.encodeToString(richText)

    fun deserialize(encoded: String): RichText = json.decodeFromString(encoded)
}
