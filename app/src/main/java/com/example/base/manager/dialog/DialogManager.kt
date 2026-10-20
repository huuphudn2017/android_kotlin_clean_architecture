package com.example.base.manager.dialog

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface DialogState {
    data object Hidden : DialogState

    data class Message(
        val title: String,
        val message: String,
        val onDismiss: (() -> Unit)? = null,
    ) : DialogState

    data class Confirm(
        val title: String,
        val message: String,
        val confirmText: String? = null,
        val isDestructive: Boolean = false,
        val onConfirm: () -> Unit,
        val onDismiss: (() -> Unit)? = null,
    ) : DialogState
}

/**
 * App-wide dialogs, callable from anywhere (ViewModels included). Rendered by [DialogHost],
 * which must be mounted once at the root of the Compose tree.
 */
object DialogManager {
    private val _state = MutableStateFlow<DialogState>(DialogState.Hidden)
    val state: StateFlow<DialogState> = _state.asStateFlow()

    fun showMessage(title: String, message: String, onDismiss: (() -> Unit)? = null) {
        _state.value = DialogState.Message(title, message, onDismiss)
    }

    fun showConfirm(
        title: String,
        message: String,
        confirmText: String? = null,
        isDestructive: Boolean = false,
        onDismiss: (() -> Unit)? = null,
        onConfirm: () -> Unit,
    ) {
        _state.value = DialogState.Confirm(title, message, confirmText, isDestructive, onConfirm, onDismiss)
    }

    fun dismiss() {
        _state.value = DialogState.Hidden
    }
}
