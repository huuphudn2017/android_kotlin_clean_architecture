package com.example.base.domain.result

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import java.net.SocketTimeoutException

class ApiErrorTest {

    @Test
    fun `keeps the original cause`() {
        val timeout = SocketTimeoutException("30s")
        assertSame(timeout, ApiError.Timeout(timeout).cause)
        val io = IOException("closed")
        assertSame(io, ApiError.Network(io).cause)
    }

    @Test
    fun `technical message contains the status code`() {
        assertTrue(ApiError.Unauthenticated(null).message!!.contains("401"))
        assertTrue(ApiError.Forbidden(null).message!!.contains("403"))
        assertTrue(ApiError.Server(503, null).message!!.contains("503"))
        assertTrue(ApiError.Http(404, null).message!!.contains("404"))
    }

    /** Compile-time check: adding a variant without handling it breaks this file. */
    @Test
    fun `every variant is handled exhaustively`() {
        val all = listOf(
            ApiError.Unauthenticated(null),
            ApiError.Forbidden(null),
            ApiError.Server(500, null),
            ApiError.Http(400, null),
            ApiError.NoConnection(IOException()),
            ApiError.Timeout(IOException()),
            ApiError.Network(IOException()),
            ApiError.Serialization(RuntimeException()),
            ApiError.Unexpected(RuntimeException()),
        )
        all.forEach { error ->
            val refreshToken = when (error) {
                is ApiError.Unauthenticated -> true
                is ApiError.Forbidden,
                is ApiError.Server,
                is ApiError.Http,
                is ApiError.NoConnection,
                is ApiError.Timeout,
                is ApiError.Network,
                is ApiError.Serialization,
                is ApiError.Unexpected -> false
            }
            assertEquals(error is ApiError.Unauthenticated, refreshToken)
        }
    }
}
