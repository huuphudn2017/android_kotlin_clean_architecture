package com.example.base.domain.result

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class ApiResultTest {

    private val anError = ApiError.Unauthenticated("expired")

    @Test
    fun `getOrNull and errorOrNull pick the right branch`() {
        assertEquals("ok", ApiResult.Success("ok").getOrNull())
        assertNull(ApiResult.Failure(anError).getOrNull())
        assertSame(anError, ApiResult.Failure(anError).errorOrNull())
        assertNull(ApiResult.Success("ok").errorOrNull())
    }

    @Test
    fun `isSuccess and fold follow the branch`() {
        assertTrue(ApiResult.Success(1).isSuccess)
        assertFalse(ApiResult.Failure(anError).isSuccess)
        assertEquals("v7", ApiResult.Success(7).fold({ "v$it" }, { "err" }))
        assertEquals("err", ApiResult.Failure(anError).fold({ "v$it" }, { "err" }))
    }

    @Test
    fun `getOrElse only runs on failure`() {
        var called = false
        assertEquals(5, ApiResult.Success(5).getOrElse { called = true; -1 })
        assertFalse(called)
        assertEquals(-1, ApiResult.Failure(anError).getOrElse { -1 })
    }

    @Test
    fun `map never runs the transform on failure`() {
        var ran = false
        val result = ApiResult.Failure(anError).map<Nothing, Int> { ran = true; 0 }
        assertFalse(ran)
        assertSame(anError, (result as ApiResult.Failure).error)
        assertEquals(ApiResult.Success(10), ApiResult.Success(5).map { it * 2 })
    }

    @Test
    fun `onSuccess and onFailure run only their branch and return the receiver`() {
        var ok = 0
        var ko = 0
        val success = ApiResult.Success(1)
        assertSame(success, success.onSuccess { ok++ }.onFailure { ko++ })
        val failure = ApiResult.Failure(anError)
        assertSame(failure, failure.onSuccess { ok++ }.onFailure { ko++ })
        assertEquals(1, ok)
        assertEquals(1, ko)
    }
}
