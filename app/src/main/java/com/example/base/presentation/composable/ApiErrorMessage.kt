package com.example.base.presentation.composable

import android.content.Context
import com.example.base.R
import com.example.base.domain.result.ApiError
import com.example.base.manager.dialog.DialogManager

/**
 * User-facing text for an [ApiError]. `ApiError.message` is for logs only.
 * Exhaustive `when`: a new [ApiError] variant fails compilation here until it is handled.
 */
fun ApiError.toUserMessage(context: Context): String = when (this) {
    is ApiError.NoConnection -> context.getString(R.string.error_no_connection)
    is ApiError.Timeout -> context.getString(R.string.error_timeout)
    is ApiError.Network -> context.getString(R.string.error_network)
    is ApiError.Unauthenticated -> context.getString(R.string.error_unauthenticated)
    is ApiError.Forbidden -> context.getString(R.string.error_forbidden)
    is ApiError.Server -> context.getString(R.string.error_server, status)
    is ApiError.Http -> serverMessage ?: context.getString(R.string.error_http, status)
    is ApiError.Serialization -> context.getString(R.string.error_serialization)
    is ApiError.Unexpected -> context.getString(R.string.error_unexpected)
}

/** Shows [error] in the global dialog. */
fun showApiError(context: Context, error: ApiError) {
    DialogManager.showMessage(
        title = context.getString(R.string.error_title),
        message = error.toUserMessage(context),
    )
}
