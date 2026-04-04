package com.blackpiratex.flowye2ee.presentation.ui.editor

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BreadcrumbBar(labels: List<String>) {
    if (labels.isEmpty()) return
    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        labels.forEachIndexed { index, label ->
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall
            )
            if (index < labels.lastIndex) {
                Text(text = " / ", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
