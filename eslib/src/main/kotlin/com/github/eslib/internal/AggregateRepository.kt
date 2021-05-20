package com.github.eslib.internal

import com.github.eslib.api.EventStore

/**
 * Aggregate repository interface
 */
class AggregateRepository<A, E>(private val factory: AggregateFactory<A>,
                                private val eventStore: EventStore
) {

    //TODO: check if list is empty and throw exception

    fun findById(aggregateName: String, aggregateId: String, eventHandler: (A, E) -> A) =
        factory.create(aggregateId).let {
            var aggregateInstance: A = it
            eventStore.loadEvents<E>(aggregateName, aggregateId)
                .forEach { event -> aggregateInstance = eventHandler(aggregateInstance, event) }
            return@let aggregateInstance;
        }
}