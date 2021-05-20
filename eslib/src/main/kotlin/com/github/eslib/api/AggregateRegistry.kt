package com.github.eslib.api

import com.github.eslib.internal.AggregateFactory
import com.github.eslib.internal.AggregateRepository
import com.github.eslib.internal.AggregateRepresentation
import com.github.eslib.internal.Handlers

/**
 * A registry for aggregate types.
 */
class AggregateRegistry(private val eventStore: EventStore) {

    class AggregateAlreadyRegisteredException(name: String) : RuntimeException("""Aggregate already registered: $name""")
    class AggregateNotFoundException(name: String) : RuntimeException("""Aggregate not registered: $name""")
    class CommandNotHandledByAnyAggregateException(command: Any) : RuntimeException("Could not find an aggregate to handle $command")

    private val aggregates: MutableMap<String, AggregateRepresentation<Any, Any, Any>> = mutableMapOf()

    /**
     * Returns the aggregate by its name
     */
    fun <A, C, E> lookupByName(name: String): AggregateRepresentation<A, C, E> {
        if (!aggregates.containsKey(name)) {
            throw AggregateNotFoundException(name)
        }
        return aggregates[name] as (AggregateRepresentation<A, C, E>)
    }

    /**
     * Returns the aggregate by the command
     */
    fun <A, C: Any, E> lookupByCommand(command: C): AggregateRepresentation<A, C, E> {
        val value =
            aggregates.values.find { it.handlesCommand(command) }
                ?: throw CommandNotHandledByAnyAggregateException(command)

        return value as AggregateRepresentation<A, C, E>
    }


    /**
     * Registers an aggregate on this registry.
     * The aggregate name is case sensitive.
     */
    fun <A, C, E> register(name: String,
                           baseCommandClass: Class<C>,
                           isNewFunction: (C) -> Boolean,
                           aggregateFactory: AggregateFactory<A>,
                           commandHandler: (A, C) -> E,
                           eventHandler: (A, E) -> A
    ) {

        if (aggregates.containsKey(name)) {
            throw AggregateAlreadyRegisteredException(name)
        }

        AggregateRepresentation(name,
                                baseCommandClass,
                                aggregateFactory,
                                AggregateRepository(aggregateFactory, eventStore),
                                eventStore,
                                Handlers(isNewFunction, commandHandler, eventHandler)
        ).also {
            aggregates[name] = it as (AggregateRepresentation<Any, Any, Any>)
        }

    }

    /**
     * Registers an aggregate on this registry.
     * The aggregate name is case sensitive.
     */
    fun <A, C, E> register(name: String,
                           baseCommandClass: Class<C>,
                           isNewFunction: (C) -> Boolean,
                           aggregateFactory: (String) -> A,
                           commandHandler: (A, C) -> E,
                           eventHandler: (A, E) -> A
    ) = register(name = name,
                 baseCommandClass = baseCommandClass,
                 isNewFunction = isNewFunction,
                 aggregateFactory = object : AggregateFactory<A> {
                     override fun create(id: String): A = aggregateFactory(id) },
                 commandHandler = commandHandler,
                 eventHandler = eventHandler)



}
