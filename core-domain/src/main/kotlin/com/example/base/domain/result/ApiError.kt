package com.example.base.domain.result

/**
 * Classified failure of a remote call, produced by the data layer.
 *
 * `message` is technical (for logs). User-facing text is resolved in the presentation layer.
 * Still an [Exception] so stack traces and `cause` survive.
 */
sealed class ApiError(message: String, cause: Throwable? = null) : Exception(message, cause) {

    /** 401 — not authenticated. Fixable with a new token. */
    class Unauthenticated(val serverMessage: String?) :
        ApiError("HTTP 401 - ${serverMessage ?: "Unauthenticated"}")

    /** 403 — authenticated but not allowed. A new token will not help. */
    class Forbidden(val serverMessage: String?) :
        ApiError("HTTP 403 - ${serverMessage ?: "Forbidden"}")

    /** 5xx — the only group worth retrying as-is. */
    class Server(val status: Int, val serverMessage: String?) :
        ApiError("HTTP $status - ${serverMessage ?: "Server error"}")

    /** Other 4xx — the request itself is wrong. */
    class Http(val status: Int, val serverMessage: String?) :
        ApiError("HTTP $status - ${serverMessage ?: "Request failed"}")

    /** Host could not be resolved (offline or DNS not ready). */
    class NoConnection(cause: Throwable) : ApiError("No internet connection", cause)

    /** Read/connect timeout. The server may still be processing the request. */
    class Timeout(cause: Throwable) : ApiError("Network timeout: ${cause.message}", cause)

    /** Any other I/O failure (connection reset, TLS handshake). */
    class Network(cause: Throwable) : ApiError("Network error: ${cause.message}", cause)

    /** Response could not be parsed — the payload shape changed. */
    class Serialization(cause: Throwable) : ApiError("Malformed response: ${cause.message}", cause)

    /** Anything else. Seeing this in logs usually means a missing variant above. */
    class Unexpected(cause: Throwable) : ApiError("Unexpected error: ${cause.message}", cause)
}
