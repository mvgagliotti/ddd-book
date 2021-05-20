package com.github.eslib.infrastructure

import com.github.eslib.api.EventStore
import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory implementation for an event store.
 */
class InMemoryEventStore : EventStore {

    private val mapOfEvents: ConcurrentHashMap<Pair<String,String>, MutableList<Any>> = ConcurrentHashMap()
    private val lock = Any()

    override fun <E> loadEvents(aggregate: String, aggregateId: String): List<E> {
        return Pair(aggregate, aggregateId).let { key ->
            if (mapOfEvents.containsKey(key)) {
                mapOfEvents[key] as List<E>
            } else {
                emptyList()
            }
        }
    }

    override fun save(aggregate: String, aggregateId: String, event: Any) {
        val pair = Pair(aggregate, aggregateId)
        if (!mapOfEvents.containsKey(pair)) {
            mapOfEvents[pair] = mutableListOf()
        }
        synchronized(lock) {
            mapOfEvents[pair]!!.add(event)
        }
    }
}