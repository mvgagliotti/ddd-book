package com.github.eslib

import com.github.eslib.infrastructure.SimpleAggregateCache
import org.junit.Assert
import org.junit.Test


class SimpleAggregateCacheTest {

    @Test
    fun `given elements added to cache must expire them at the specified time` () {
        val cache = SimpleAggregateCache(1000)
        cache.put(Pair("1", "1"), "Value 1")

        Thread.sleep(2000)

        cache.put(Pair("1", "2"), "Value 2")

        Assert.assertNull(cache.get(Pair("1", "1")))
        Assert.assertNotNull(cache.get(Pair("1", "2")))

        Thread.sleep(4000)

        Assert.assertNull(cache.get(Pair("1", "2")))

        cache.finish()
    }
}