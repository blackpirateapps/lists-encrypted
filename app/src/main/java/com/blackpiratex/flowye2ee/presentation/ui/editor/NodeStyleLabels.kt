package com.blackpiratex.flowye2ee.presentation.ui.editor

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun StyleLabel(text: String) {
    Text(text = text, style = MaterialTheme.typography.bodySmall)
}
