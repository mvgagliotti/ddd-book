package com.github.eslib.api


/**
 * Class responsible to process commands.
 */
class Command(private val registry: AggregateRegistry,
              private val eventStore: EventStore,
              private val cache: AggregateCache
) {

    /**
     * Process a command for the given aggregate
     */
    fun <E> process(entityId: String, command: Any): E {

        val aggregateRepresentation = registry.lookupByCommand<Any, Any, E>(command)

        val instance = if (aggregateRepresentation.isNew(command)) {
            aggregateRepresentation.create(entityId)
        } else {
            aggregateRepresentation.loadById(entityId)
        }.also { cache.put(Pair(aggregateRepresentation.name, entityId), it) }

        //handles the command
        val event = aggregateRepresentation.handleCommand(instance, command)

        //saves the event
        eventStore.save(aggregateRepresentation.name, entityId, event as Any)

        //handles the event to apply the changes to the aggregate
        //TODO: what to do with the generated aggregate?
        val freshInstance = aggregateRepresentation.handleEvent(instance, event)

        return event
    }

}
