package com.blackpiratex.flowye2ee.presentation.ui.editor

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.Modifier

@Composable
fun shortcutHandler(
    onBold: () -> Unit,
    onItalic: () -> Unit,
    onUnderline: () -> Unit
): Modifier {
    return Modifier.onKeyEvent { event ->
        if (event.type == KeyEventType.KeyDown && event.isCtrlPressed) {
            when (event.key) {
                Key.B -> {
                    onBold(); true
                }
                Key.I -> {
                    onItalic(); true
                }
                Key.U -> {
                    onUnderline(); true
                }
                else -> false
            }
        } else {
            false
        }
    }
}
