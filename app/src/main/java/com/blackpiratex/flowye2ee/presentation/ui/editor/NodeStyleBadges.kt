package com.blackpiratex.flowye2ee.presentation.ui.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HeadingBadge(level: Int) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
    ) {
        Box(modifier = Modifier.width(20.dp + (level * 2).dp).height(10.dp + level.dp))
    }
}
