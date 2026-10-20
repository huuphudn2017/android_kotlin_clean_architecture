package com.example.base.data.network

import com.google.gson.JsonParseException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import com.example.base.domain.result.ApiError
import com.example.base.domain.result.ApiResult
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Pins the catch order of [apiCall]. The exception types are related
 * (`SocketTimeoutException` < `InterruptedIOException` < `IOException`,
 * `UnknownHostException` < `IOException`, `CancellationException` < `RuntimeException`),
 * so a wrong order compiles and silently misclassifies.
 */
class ApiCallTest {

    private fun httpError(code: Int, body: String = ""): HttpException =
        HttpException(Response.error<Any>(code, body.toResponseBody("application/json".toMediaType())))

    private suspend fun errorOf(block: suspend () -> Any): ApiError =
        (apiCall(block) as ApiResult.Failure).error

    @Test
    fun `success is wrapped`() = runTest {
        assertEquals(ApiResult.Success(42), apiCall { 42 })
    }

    @Test
    fun `cancellation is rethrown, not turned into a failure`() = runTest {
        val cancellation = CancellationException("cancelled")
        try {
            apiCall { throw cancellation }
            fail("CancellationException must be rethrown")
        } catch (e: CancellationException) {
            assertSame(cancellation, e)
        }
    }

    @Test
    fun `http status codes map to distinct errors`() = runTest {
        assertTrue(errorOf { throw httpError(401) } is ApiError.Unauthenticated)
        assertTrue(errorOf { throw httpError(403) } is ApiError.Forbidden)
        assertEquals(503, (errorOf { throw httpError(503) } as ApiError.Server).status)
        assertEquals(404, (errorOf { throw httpError(404) } as ApiError.Http).status)
    }

    @Test
    fun `io subclasses are classified before generic io`() = runTest {
        assertTrue(errorOf { throw UnknownHostException("api") } is ApiError.NoConnection)
        assertTrue(errorOf { throw SocketTimeoutException("t") } is ApiError.Timeout)
        assertTrue(errorOf { throw IOException("closed") } is ApiError.Network)
    }

    @Test
    fun `parse and unknown errors`() = runTest {
        assertTrue(errorOf { throw JsonParseException("bad") } is ApiError.Serialization)
        assertTrue(errorOf { throw IllegalStateException("bug") } is ApiError.Unexpected)
    }

    @Test
    fun `server message is read from json body, raw body kept otherwise`() = runTest {
        val json = errorOf { throw httpError(404, """{"message":"Not found"}""") } as ApiError.Http
        assertEquals("Not found", json.serverMessage)
        val raw = errorOf { throw httpError(500, "<html>Bad Gateway</html>") } as ApiError.Server
        assertEquals("<html>Bad Gateway</html>", raw.serverMessage)
        assertNull((errorOf { throw httpError(500) } as ApiError.Server).serverMessage)
    }
}
