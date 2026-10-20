package com.example.base.presentation.todo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.base.R
import com.example.base.domain.entity.Todo
import com.example.base.domain.entity.TodoFilter
import com.example.base.domain.usecase.TodoUseCase
import com.example.base.manager.dialog.DialogManager
import com.example.base.manager.navigation.AppRoute
import com.example.base.manager.navigation.NavigatorManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TodoViewModel @Inject constructor(
    application: Application,
    private val todoUseCase: TodoUseCase,
    private val navigator: NavigatorManager,
) : AndroidViewModel(application) {

    private val filter = MutableStateFlow(TodoFilter.ALL)
    private val editor = MutableStateFlow<TodoEditor?>(null)

    private val _events = Channel<TodoEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    val state: StateFlow<TodoState> = combine(
        filter,
        filter.flatMapLatest { todoUseCase.observe(it) },
        todoUseCase.observe(TodoFilter.ALL),
        editor,
    ) { filter, items, all, editor ->
        TodoState(
            isLoading = false,
            items = items,
            filter = filter,
            remainingCount = all.count { !it.isDone },
            hasDone = all.any { it.isDone },
            editor = editor,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TodoState())

    fun setFilter(value: TodoFilter) = filter.update { value }

    fun toggle(todo: Todo) {
        viewModelScope.launch { todoUseCase.toggle(todo) }
    }

    fun delete(todo: Todo) {
        viewModelScope.launch {
            todoUseCase.delete(todo)
            _events.send(TodoEvent.Deleted(todo))
        }
    }

    fun undoDelete(todo: Todo) {
        viewModelScope.launch { todoUseCase.restore(todo) }
    }

    fun clearDone() {
        val app = getApplication<Application>()
        DialogManager.showConfirm(
            title = app.getString(R.string.todo_clear_done),
            message = app.getString(R.string.todo_clear_done_confirm),
            confirmText = app.getString(R.string.action_delete),
            isDestructive = true,
        ) {
            viewModelScope.launch { todoUseCase.clearDone() }
        }
    }

    // Editor

    fun openEditor(todo: Todo? = null) = editor.update { TodoEditor(todo) }

    fun onEditorTextChange(text: String) = editor.update { it?.copy(text = text) }

    fun closeEditor() = editor.update { null }

    fun saveEditor() {
        val current = editor.value ?: return
        viewModelScope.launch {
            val saved = current.todo?.let { todoUseCase.rename(it, current.text) }
                ?: todoUseCase.add(current.text)
            if (saved) closeEditor()
        }
    }

    fun openSetting() = navigator.navigate(AppRoute.Setting)
}
