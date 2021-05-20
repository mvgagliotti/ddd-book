package com.github.eslib.internal

import com.github.eslib.api.EventStore

/**
 * Internal representation of the aggregate: a data structure to store the aggregate information.
 * Also, provides methods to handle commands and events on aggregates of a given type
 */
data class AggregateRepresentation<A, C, E>(
        val name: String,
        private val baseCommandClass: Class<C>,
        private val aggregateFactory: AggregateFactory<A>,
        private val aggregateRepository: AggregateRepository<A, E>,
        private val eventStore: EventStore,
        private val handlers: Handlers<A, C, E>
) {
    fun <C: Any> handlesCommand(command: C) = baseCommandClass.isAssignableFrom(command.javaClass)
    fun isNew(command: C) = handlers.isNewHandler(command)
    fun create(id: String) = aggregateFactory.create(id)
    fun loadById(id: String) = aggregateRepository.findById(name, id, handlers.eventHandler)
    fun handleCommand(instance: A, command: C): E = handlers.commandHandler(instance, command)
    fun handleEvent(instance: A, event: E): A = handlers.eventHandler(instance, event)
}

data class Handlers<A, C, E>(
    val isNewHandler: (C) -> Boolean,
    val commandHandler: (A, C) -> E,
    val eventHandler: (A, E) -> A
)