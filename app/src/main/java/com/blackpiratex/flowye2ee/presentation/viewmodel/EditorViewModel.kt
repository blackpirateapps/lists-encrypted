package com.blackpiratex.flowye2ee.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blackpiratex.flowye2ee.data.local.entity.NodeEntity
import com.blackpiratex.flowye2ee.data.repository.NodeRepository
import com.blackpiratex.flowye2ee.domain.model.NodeStyle
import com.blackpiratex.flowye2ee.domain.model.RichText
import com.blackpiratex.flowye2ee.domain.model.SlashCommand
import com.blackpiratex.flowye2ee.domain.model.SpanRange
import com.blackpiratex.flowye2ee.domain.model.SpanStyleType
import com.blackpiratex.flowye2ee.domain.usecase.NodeUseCases
import com.blackpiratex.flowye2ee.presentation.state.EditorState
import com.blackpiratex.flowye2ee.presentation.state.NodeUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditorViewModel(
    private val repository: NodeRepository
): ViewModel() {
    private val useCases = NodeUseCases(repository)
    private val _state = MutableStateFlow(EditorState())
    val state: StateFlow<EditorState> = _state

    fun load() {
        viewModelScope.launch {
            useCases.seedDemoIfEmpty()
            val nodes = repository.loadAllDomainNodes()
            val flattened = flatten(nodes)
            _state.value = _state.value.copy(nodes = flattened)
        }
    }

    private fun flatten(nodes: List<com.blackpiratex.flowye2ee.domain.model.Node>): List<NodeUi> {
        val grouped = nodes.groupBy { it.parentId }
        val result = mutableListOf<NodeUi>()
        val breadcrumbLabels = mutableListOf<String>()
        val lookup = nodes.associateBy { it.id }
        var current = _state.value.zoomNodeId
        while (current != null) {
            val node = lookup[current] ?: break
            breadcrumbLabels.add(node.content.text.ifBlank { "(untitled)" })
            current = node.parentId
        }
        _state.value = _state.value.copy(breadcrumb = breadcrumbLabels.reversed())
        fun walk(parentId: String?, depth: Int) {
            grouped[parentId].orEmpty().sortedBy { it.position }.forEach { node ->
                result.add(
                    NodeUi(
                        id = node.id,
                        parentId = node.parentId,
                        content = node.content,
                        style = node.style,
                        isCompleted = node.isCompleted,
                        position = node.position,
                        isCollapsed = node.isCollapsed,
                        depth = depth,
                        hasChildren = grouped[node.id].orEmpty().isNotEmpty()
                    )
                )
                if (!node.isCollapsed) {
                    walk(node.id, depth + 1)
                }
            }
        }
        walk(_state.value.zoomNodeId, 0)
        return result
    }

    fun updateText(nodeId: String, newText: String) {
        viewModelScope.launch {
            val entity = repository.loadAllNodes().firstOrNull { it.id == nodeId } ?: return@launch
            val content = repository.getNodeContent(entity)
            val updated = content.copy(text = newText)
            repository.updateNodeContent(entity, updated)
            refresh()
        }
    }

    fun updateRichText(nodeId: String, newText: String, spans: List<com.blackpiratex.flowye2ee.domain.model.SpanRange>) {
        viewModelScope.launch {
            val entity = repository.loadAllNodes().firstOrNull { it.id == nodeId } ?: return@launch
            val updated = com.blackpiratex.flowye2ee.domain.model.RichText(text = newText, spans = spans)
            repository.updateNodeContent(entity, updated)
            refresh()
        }
    }

    fun createSibling(afterNode: NodeUi) {
        viewModelScope.launch {
            val position = afterNode.position + 1
            val newNode = useCases.createNode(
                parentId = afterNode.parentId,
                text = RichText("", emptyList()),
                style = NodeStyle.BULLET,
                position = position
            )
            shiftPositions(afterNode.parentId, position, newNode.id)
            refresh()
        }
    }

    private suspend fun shiftPositions(parentId: String?, from: Int, excludeId: String?) {
        val nodes = repository.loadAllNodes().filter { it.parentId == parentId && it.id != excludeId }
        nodes.filter { it.position >= from }.sortedBy { it.position }.forEachIndexed { index, node ->
            repository.updateNode(node.copy(position = from + index + 1))
        }
    }

    fun toggleTodo(nodeId: String) {
        viewModelScope.launch {
            val node = repository.loadAllNodes().firstOrNull { it.id == nodeId } ?: return@launch
            useCases.toggleTodo(node)
            refresh()
        }
    }

    fun deleteNode(nodeId: String) {
        viewModelScope.launch {
            repository.deleteSubtree(nodeId)
            refresh()
        }
    }

    fun toggleCollapse(nodeId: String) {
        viewModelScope.launch {
            val node = repository.loadAllNodes().firstOrNull { it.id == nodeId } ?: return@launch
            useCases.toggleCollapse(node)
            refresh()
        }
    }

    fun indent(nodeId: String) {
        viewModelScope.launch {
            val node = repository.loadAllNodes().firstOrNull { it.id == nodeId } ?: return@launch
            useCases.indent(node)
            refresh()
        }
    }

    fun unindent(nodeId: String) {
        viewModelScope.launch {
            val node = repository.loadAllNodes().firstOrNull { it.id == nodeId } ?: return@launch
            useCases.unindent(node)
            refresh()
        }
    }

    fun applySlashCommand(nodeId: String, command: SlashCommand) {
        viewModelScope.launch {
            val node = repository.loadAllNodes().firstOrNull { it.id == nodeId } ?: return@launch
            useCases.updateStyle(node, command.style)
            _state.value = _state.value.copy(isSlashMenuOpen = false, slashQuery = "")
            refresh()
        }
    }

    fun setSlashQuery(query: String) {
        _state.value = _state.value.copy(
            isSlashMenuOpen = query.startsWith("/"),
            slashQuery = query
        )
    }

    fun applyInlineStyle(nodeId: String, style: SpanStyleType) {
        viewModelScope.launch {
            val entity = repository.loadAllNodes().firstOrNull { it.id == nodeId } ?: return@launch
            val content = repository.getNodeContent(entity)
            val span = SpanRange(0, content.text.length, style)
            val updated = content.copy(spans = content.spans + span)
            repository.updateNodeContent(entity, updated)
            refresh()
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            val nodes = repository.loadAllDomainNodes()
            val results = nodes.filter { it.content.text.contains(query, ignoreCase = true) }
                .map { node ->
                    NodeUi(
                        id = node.id,
                        parentId = node.parentId,
                        content = node.content,
                        style = node.style,
                        isCompleted = node.isCompleted,
                        position = node.position,
                        isCollapsed = node.isCollapsed,
                        depth = 0,
                        hasChildren = false
                    )
                }
            _state.value = _state.value.copy(searchQuery = query, searchResults = results)
        }
    }

    fun zoomInto(nodeId: String?) {
        _state.value = _state.value.copy(zoomNodeId = nodeId)
        refresh()
    }

    fun setFocused(nodeId: String) {
        _state.value = _state.value.copy(focusedNodeId = nodeId)
    }

    private fun refresh() {
        viewModelScope.launch {
            val nodes = repository.loadAllDomainNodes()
            _state.value = _state.value.copy(nodes = flatten(nodes))
        }
    }
}
