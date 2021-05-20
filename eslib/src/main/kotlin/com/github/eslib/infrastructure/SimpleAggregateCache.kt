package com.github.eslib.infrastructure

import com.github.eslib.api.AggregateCache
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.schedule

/**
 * Simple cache to store aggregates.
 */
class SimpleAggregateCache(private val timeToExpire: Long) : AggregateCache {

    private data class CacheValue(val time: Instant, val instance: Any)

    private val cache = ConcurrentHashMap<Pair<String, String>, CacheValue>()
    private val timer = Timer()
    private val timerTask: TimerTask = timer.schedule(Date.from(Instant.now()), 1000) {
        cache.entries
            .map { it.key }
            .filter { Duration.between(cache[it]?.time, Instant.now()).toMillis() > timeToExpire }
            .forEach(cache::remove)
    }

    override fun put(key: Pair<String, String>, value: Any) {
        cache[key] = CacheValue(Instant.now(), value)
    }

    override fun get(key: Pair<String, String>): Any? = cache[key]?.instance

    override fun finish() {
        timerTask.cancel()
        timer.cancel()
    }
}
