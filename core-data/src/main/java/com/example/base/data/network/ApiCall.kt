package com.example.base.data.network

import com.google.gson.Gson
import com.google.gson.JsonParseException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import com.example.base.domain.result.ApiError
import com.example.base.domain.result.ApiResult
import java.io.IOException
import java.io.InterruptedIOException
import java.net.UnknownHostException

/**
 * Wraps a Retrofit call and classifies failures into [ApiError].
 *
 * `internal` on purpose: only `*RepositoryImpl` in this module should call it, so the domain
 * layer always sees typed errors. Map DTOs to entities *inside* the block so parse errors
 * become [ApiError.Serialization].
 *
 * The catch order matters and still compiles when wrong — see `ApiCallTest`.
 */
internal suspend fun <T> apiCall(block: suspend () -> T): ApiResult<T> {
    return try {
        ApiResult.Success(block())
    } catch (e: CancellationException) {
        // Must be rethrown before `catch (Exception)`, otherwise cancelled work keeps running.
        throw e
    } catch (e: HttpException) {
        ApiResult.Failure(e.toApiError())
    } catch (e: UnknownHostException) {
        ApiResult.Failure(ApiError.NoConnection(e))
    } catch (e: InterruptedIOException) {
        // SocketTimeoutException extends this; must come before IOException.
        ApiResult.Failure(ApiError.Timeout(e))
    } catch (e: IOException) {
        ApiResult.Failure(ApiError.Network(e))
    } catch (e: JsonParseException) {
        ApiResult.Failure(ApiError.Serialization(e))
    } catch (e: Exception) {
        ApiResult.Failure(ApiError.Unexpected(e))
    }
}

private fun HttpException.toApiError(): ApiError {
    val status = code()
    val serverMessage = parseErrorBody(runCatching { response()?.errorBody()?.string() }.getOrNull())
    return when {
        status == 401 -> ApiError.Unauthenticated(serverMessage)
        status == 403 -> ApiError.Forbidden(serverMessage)
        status >= 500 -> ApiError.Server(status, serverMessage)
        else -> ApiError.Http(status, serverMessage)
    }
}

/** Expected error body: `{"message": "..."}`. Adjust to your backend. */
private class ApiErrorBody {
    val message: String? = null
}

private fun parseErrorBody(errorBody: String?): String? {
    if (errorBody.isNullOrBlank()) return null
    return try {
        Gson().fromJson(errorBody, ApiErrorBody::class.java)?.message ?: errorBody
    } catch (e: Exception) {
        errorBody
    }
}
