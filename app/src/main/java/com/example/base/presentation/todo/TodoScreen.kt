package com.example.base.presentation.todo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.base.R
import com.example.base.domain.entity.TodoFilter
import com.example.base.manager.ClickThrottleManager
import com.example.base.presentation.composable.EmptyState
import com.example.base.presentation.todo.composable.TodoEditorDialog
import com.example.base.presentation.todo.composable.TodoItem
import com.example.base.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(viewModel: TodoViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val deletedMessage = stringResource(R.string.todo_deleted)
    val undoLabel = stringResource(R.string.todo_undo)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is TodoEvent.Deleted -> {
                    val result = snackbar.showSnackbar(
                        message = deletedMessage,
                        actionLabel = undoLabel,
                        duration = SnackbarDuration.Short,
                    )
                    if (result == SnackbarResult.ActionPerformed) viewModel.undoDelete(event.todo)
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.todo_title)) },
                actions = {
                    IconButton(onClick = viewModel::openSetting) {
                        Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.setting_title))
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { ClickThrottleManager.throttle("todo_add") { viewModel.openEditor() } }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.action_add))
            }
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            FilterBar(
                selected = state.filter,
                remaining = state.remainingCount,
                showClearDone = state.hasDone,
                onSelect = viewModel::setFilter,
                onClearDone = viewModel::clearDone,
            )
            HorizontalDivider()
            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                state.items.isEmpty() -> EmptyState(
                    icon = Icons.Filled.CheckCircle,
                    title = stringResource(R.string.todo_empty),
                    message = stringResource(R.string.todo_empty_hint),
                )
                else -> LazyColumn(contentPadding = PaddingValues(bottom = 88.dp)) {
                    items(state.items, key = { it.id }) { todo ->
                        TodoItem(
                            todo = todo,
                            onToggle = { viewModel.toggle(todo) },
                            onEdit = { viewModel.openEditor(todo) },
                            onDelete = { viewModel.delete(todo) },
                            modifier = Modifier.animateItem(),
                        )
                    }
                }
            }
        }
    }

    state.editor?.let { editor ->
        TodoEditorDialog(
            editor = editor,
            onTextChange = viewModel::onEditorTextChange,
            onSave = viewModel::saveEditor,
            onDismiss = viewModel::closeEditor,
        )
    }
}

@Composable
private fun FilterBar(
    selected: TodoFilter,
    remaining: Int,
    showClearDone: Boolean,
    onSelect: (TodoFilter) -> Unit,
    onClearDone: () -> Unit,
) {
    Column(Modifier.fillMaxWidth().padding(horizontal = Spacing.md)) {
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            TodoFilter.entries.forEach { filter ->
                FilterChip(
                    selected = filter == selected,
                    onClick = { onSelect(filter) },
                    label = { Text(stringResource(filter.labelRes)) },
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = pluralStringResource(R.plurals.todo_remaining, remaining, remaining),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            if (showClearDone) {
                TextButton(onClick = onClearDone) { Text(stringResource(R.string.todo_clear_done)) }
            }
        }
    }
}

private val TodoFilter.labelRes: Int
    get() = when (this) {
        TodoFilter.ALL -> R.string.todo_filter_all
        TodoFilter.ACTIVE -> R.string.todo_filter_active
        TodoFilter.DONE -> R.string.todo_filter_done
    }
