package com.blackpiratex.flowye2ee.presentation.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BulletMarker(color: Color) {
    Box(
        modifier = Modifier
            .width(6.dp)
            .height(6.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
fun QuoteMarker(color: Color) {
    Box(
        modifier = Modifier
            .width(4.dp)
            .height(18.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
fun DividerMarker() {
    androidx.compose.material3.Divider()
}
