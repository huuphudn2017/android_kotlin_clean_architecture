package com.example.base.domain.result

/**
 * Result of a remote call. Like `kotlin.Result`, but the failure is a sealed [ApiError],
 * so `when (error)` is exhaustive.
 */
sealed interface ApiResult<out T> {
    data class Success<out T>(val value: T) : ApiResult<T>
    data class Failure(val error: ApiError) : ApiResult<Nothing>
}

inline fun <T> ApiResult<T>.onSuccess(action: (T) -> Unit): ApiResult<T> {
    if (this is ApiResult.Success) action(value)
    return this
}

inline fun <T> ApiResult<T>.onFailure(action: (ApiError) -> Unit): ApiResult<T> {
    if (this is ApiResult.Failure) action(error)
    return this
}

inline fun <T, R> ApiResult<T>.fold(
    onSuccess: (T) -> R,
    onFailure: (ApiError) -> R,
): R = when (this) {
    is ApiResult.Success -> onSuccess(value)
    is ApiResult.Failure -> onFailure(error)
}

inline fun <R, T : R> ApiResult<T>.getOrElse(onFailure: (ApiError) -> R): R = when (this) {
    is ApiResult.Success -> value
    is ApiResult.Failure -> onFailure(error)
}

inline fun <T, R> ApiResult<T>.map(transform: (T) -> R): ApiResult<R> = when (this) {
    is ApiResult.Success -> ApiResult.Success(transform(value))
    is ApiResult.Failure -> this
}

fun <T> ApiResult<T>.getOrNull(): T? = (this as? ApiResult.Success)?.value

fun <T> ApiResult<T>.errorOrNull(): ApiError? = (this as? ApiResult.Failure)?.error

val ApiResult<*>.isSuccess: Boolean get() = this is ApiResult.Success
