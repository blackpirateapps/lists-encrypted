package com.blackpiratex.flowye2ee.presentation.ui.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TabletLayout(
    outline: @Composable () -> Unit,
    focusPane: @Composable () -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(0.4f).fillMaxHeight()) {
            outline()
        }
        Divider(modifier = Modifier.fillMaxHeight())
        Box(modifier = Modifier.weight(0.6f).fillMaxHeight()) {
            focusPane()
        }
    }
}
