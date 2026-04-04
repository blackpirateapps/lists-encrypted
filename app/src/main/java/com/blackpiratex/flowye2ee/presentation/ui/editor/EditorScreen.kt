package com.blackpiratex.flowye2ee.presentation.ui.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.blackpiratex.flowye2ee.domain.model.NodeStyle
import com.blackpiratex.flowye2ee.domain.model.SlashCommand
import com.blackpiratex.flowye2ee.presentation.state.NodeUi
import com.blackpiratex.flowye2ee.presentation.viewmodel.EditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: EditorViewModel,
    onOpenSettings: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.load()
    }

    val isTablet = LocalConfiguration.current.smallestScreenWidthDp >= 600
    val listContent: @Composable () -> Unit = {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    shortcutHandler(
                        onBold = { state.focusedNodeId?.let { viewModel.applyInlineStyle(it, com.blackpiratex.flowye2ee.domain.model.SpanStyleType.BOLD) } },
                        onItalic = { state.focusedNodeId?.let { viewModel.applyInlineStyle(it, com.blackpiratex.flowye2ee.domain.model.SpanStyleType.ITALIC) } },
                        onUnderline = { state.focusedNodeId?.let { viewModel.applyInlineStyle(it, com.blackpiratex.flowye2ee.domain.model.SpanStyleType.UNDERLINE) } }
                    )
                )
        ) {
            TopAppBar(
                title = { Text(text = "FlowyE2EE") },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            )
            BreadcrumbBar(state.breadcrumb)
            SearchBar(value = state.searchQuery, onChange = { viewModel.search(it) })
            FormattingToolbar(
                onBold = { state.focusedNodeId?.let { viewModel.applyInlineStyle(it, com.blackpiratex.flowye2ee.domain.model.SpanStyleType.BOLD) } },
                onItalic = { state.focusedNodeId?.let { viewModel.applyInlineStyle(it, com.blackpiratex.flowye2ee.domain.model.SpanStyleType.ITALIC) } },
                onUnderline = { state.focusedNodeId?.let { viewModel.applyInlineStyle(it, com.blackpiratex.flowye2ee.domain.model.SpanStyleType.UNDERLINE) } },
                onStrike = { state.focusedNodeId?.let { viewModel.applyInlineStyle(it, com.blackpiratex.flowye2ee.domain.model.SpanStyleType.STRIKETHROUGH) } },
                onCode = { state.focusedNodeId?.let { viewModel.applyInlineStyle(it, com.blackpiratex.flowye2ee.domain.model.SpanStyleType.INLINE_CODE) } },
                onLink = { state.focusedNodeId?.let { viewModel.applyInlineStyle(it, com.blackpiratex.flowye2ee.domain.model.SpanStyleType.LINK) } }
            )
            Divider()
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                val list = if (state.searchQuery.isNotBlank()) state.searchResults else state.nodes
                items(list, key = { it.id }) { node ->
                    NodeRow(
                        node = node,
                        onToggleTodo = { viewModel.toggleTodo(node.id) },
                        onToggleCollapse = { viewModel.toggleCollapse(node.id) },
                        onTextChange = { text -> viewModel.updateText(node.id, text) },
                        onSlash = { query -> viewModel.setSlashQuery(query) },
                        onApplyCommand = { command -> viewModel.applySlashCommand(node.id, command) },
                        onDelete = { viewModel.deleteNode(node.id) },
                        onCreateSibling = { viewModel.createSibling(node) },
                        onIndent = { viewModel.indent(node.id) },
                        onUnindent = { viewModel.unindent(node.id) },
                        onFocus = { viewModel.setFocused(node.id) }
                    )
                }
            }
        }
    }

    if (isTablet) {
        TabletLayout(
            outline = listContent,
            focusPane = listContent
        )
    } else {
        listContent()
    }
}

@Composable
private fun NodeRow(
    node: NodeUi,
    onToggleTodo: () -> Unit,
    onToggleCollapse: () -> Unit,
    onTextChange: (String) -> Unit,
    onSlash: (String) -> Unit,
    onApplyCommand: (SlashCommand) -> Unit,
    onDelete: () -> Unit,
    onCreateSibling: () -> Unit,
    onIndent: () -> Unit,
    onUnindent: () -> Unit,
    onFocus: () -> Unit
) {
    var textValue by remember(node.id) { mutableStateOf(TextFieldValue(node.content.text)) }
    val indent = (node.depth * 16).dp
    Column(modifier = Modifier.fillMaxWidth().padding(start = indent, end = 12.dp, top = 8.dp, bottom = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (node.hasChildren) {
                IconButton(onClick = onToggleCollapse) {
                    Icon(
                        imageVector = if (node.isCollapsed) Icons.Filled.ChevronRight else Icons.Filled.ExpandMore,
                        contentDescription = "Toggle"
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(40.dp))
            }
            when (node.style) {
                NodeStyle.DIVIDER -> Divider(modifier = Modifier.fillMaxWidth())
                else -> {
                    when (node.style) {
                        NodeStyle.BULLET -> BulletMarker(MaterialTheme.colorScheme.onSurface)
                        NodeStyle.NUMBERED -> NumberMarker(node.position + 1)
                        NodeStyle.TODO -> Checkbox(checked = node.isCompleted, onCheckedChange = { onToggleTodo() })
                        NodeStyle.PARAGRAPH -> StyleLabel("P")
                        NodeStyle.QUOTE -> QuoteMarker(MaterialTheme.colorScheme.secondary)
                        NodeStyle.HEADING_1 -> HeadingBadge(1)
                        NodeStyle.HEADING_2 -> HeadingBadge(2)
                        NodeStyle.HEADING_3 -> HeadingBadge(3)
                        NodeStyle.DIVIDER -> Spacer(modifier = Modifier.width(12.dp))
                    }
                    TextField(
                        value = textValue,
                        onValueChange = {
                            textValue = it
                            onTextChange(it.text)
                            onSlash(it.text)
                        },
                        textStyle = styleFor(node),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .onFocusChanged { if (it.isFocused) onFocus() }
                            .onKeyEvent { event ->
                                when {
                                    event.type == KeyEventType.KeyUp && event.key == Key.Backspace && textValue.text.isBlank() -> {
                                        onDelete()
                                        true
                                    }
                                    event.type == KeyEventType.KeyUp && event.key == Key.Enter -> {
                                        onCreateSibling()
                                        true
                                    }
                                    event.type == KeyEventType.KeyUp && event.key == Key.Tab -> {
                                        if (event.isShiftPressed) {
                                            onUnindent()
                                        } else {
                                            onIndent()
                                        }
                                        true
                                    }
                                    else -> false
                                }
                            },
                        placeholder = { if (node.style == NodeStyle.DIVIDER) Text("Divider") else null }
                    )
                    SlashMenu(query = textValue.text, onApply = onApplyCommand)
                }
            }
        }
    }
}

@Composable
private fun SlashMenu(query: String, onApply: (SlashCommand) -> Unit) {
    var expanded by remember(query) { mutableStateOf(query.startsWith("/")) }
    val matches = SlashCommand.entries.filter { it.token.startsWith(query) }
    DropdownMenu(expanded = expanded && matches.isNotEmpty(), onDismissRequest = { expanded = false }) {
        matches.forEach { command ->
            DropdownMenuItem(
                text = { Text(command.token) },
                onClick = {
                    expanded = false
                    onApply(command)
                }
            )
        }
    }
}


@Composable
private fun NumberMarker(number: Int) {
    Text(
        text = "$number.",
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(end = 8.dp)
    )
}


@Composable
private fun styleFor(node: NodeUi) = when (node.style) {
    NodeStyle.HEADING_1 -> MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
    NodeStyle.HEADING_2 -> MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold)
    NodeStyle.HEADING_3 -> MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium)
    NodeStyle.QUOTE -> MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic)
    NodeStyle.PARAGRAPH -> MaterialTheme.typography.bodyLarge
    else -> MaterialTheme.typography.bodyLarge
}
