package com.example.base.manager.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import com.example.base.R

@Composable
fun DialogHost() {
    when (val dialog = DialogManager.state.collectAsState().value) {
        DialogState.Hidden -> Unit

        is DialogState.Message -> {
            val close: () -> Unit = {
                DialogManager.dismiss()
                dialog.onDismiss?.invoke()
            }
            AlertDialog(
                onDismissRequest = close,
                title = { Text(dialog.title) },
                text = { Text(dialog.message) },
                confirmButton = { TextButton(onClick = close) { Text(stringResource(R.string.action_ok)) } },
            )
        }

        is DialogState.Confirm -> {
            val cancel: () -> Unit = {
                DialogManager.dismiss()
                dialog.onDismiss?.invoke()
            }
            AlertDialog(
                onDismissRequest = cancel,
                title = { Text(dialog.title) },
                text = { Text(dialog.message) },
                confirmButton = {
                    TextButton(onClick = {
                        DialogManager.dismiss()
                        dialog.onConfirm()
                    }) {
                        val color = if (dialog.isDestructive) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.primary
                        Text(dialog.confirmText ?: stringResource(R.string.action_ok), color = color)
                    }
                },
                dismissButton = {
                    TextButton(onClick = cancel) { Text(stringResource(R.string.action_cancel)) }
                },
            )
        }
    }
}
