package com.blackpiratex.flowye2ee.presentation.ui.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TabletLayout(
    outline: @Composable () -> Unit,
    focusPane: @Composable () -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val leftWidth = maxWidth * 0.4f
        val rightWidth = maxWidth - leftWidth
        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.width(leftWidth).fillMaxHeight()) {
                outline()
            }
            Divider(modifier = Modifier.fillMaxHeight())
            Box(modifier = Modifier.width(rightWidth).fillMaxHeight()) {
                focusPane()
            }
        }
    }
}
