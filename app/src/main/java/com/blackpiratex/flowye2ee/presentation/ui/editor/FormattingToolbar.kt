package com.blackpiratex.flowye2ee.presentation.ui.editor

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.StrikethroughS
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Link

@Composable
fun FormattingToolbar(
    onBold: () -> Unit,
    onItalic: () -> Unit,
    onUnderline: () -> Unit,
    onStrike: () -> Unit,
    onCode: () -> Unit,
    onLink: () -> Unit
) {
    Surface(tonalElevation = 2.dp) {
        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
            IconButton(onClick = onBold) { Icon(Icons.Filled.FormatBold, contentDescription = "Bold") }
            IconButton(onClick = onItalic) { Icon(Icons.Filled.FormatItalic, contentDescription = "Italic") }
            IconButton(onClick = onUnderline) { Icon(Icons.Filled.FormatUnderlined, contentDescription = "Underline") }
            IconButton(onClick = onStrike) { Icon(Icons.Filled.StrikethroughS, contentDescription = "Strike") }
            IconButton(onClick = onCode) { Icon(Icons.Filled.Code, contentDescription = "Code") }
            IconButton(onClick = onLink) { Icon(Icons.Filled.Link, contentDescription = "Link") }
            Text(text = "Formatting", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 6.dp))
        }
    }
}
