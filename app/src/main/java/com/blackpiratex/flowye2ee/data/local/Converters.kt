package com.blackpiratex.flowye2ee.data.local

import androidx.room.TypeConverter
import com.blackpiratex.flowye2ee.domain.model.NodeStyle

class Converters {
    @TypeConverter
    fun toNodeStyle(value: String): NodeStyle = NodeStyle.valueOf(value)

    @TypeConverter
    fun fromNodeStyle(style: NodeStyle): String = style.name
}
