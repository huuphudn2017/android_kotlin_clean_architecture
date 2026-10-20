package com.example.base.manager

import android.os.SystemClock

/** Drops repeated taps on the same action within [DEFAULT_INTERVAL_MS]. Main thread only. */
object ClickThrottleManager {
    const val DEFAULT_INTERVAL_MS = 500L

    private val lastClicks = mutableMapOf<String, Long>()

    fun throttle(key: String, intervalMs: Long = DEFAULT_INTERVAL_MS, action: () -> Unit) {
        val now = SystemClock.elapsedRealtime()
        val last = lastClicks[key]
        if (last != null && now - last < intervalMs) return
        lastClicks[key] = now
        action()
    }
}
