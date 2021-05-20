package com.github.eslib.api

/**
 * Interface that defines the contract for an event store.
 */
interface EventStore {

    /**
     * Loads the events of a given aggregate.
     *
     * @param aggregate the name of the aggregate: i.e: "UserAggregate"; It's case-sensitive.
     * @param aggregateId the id of the aggregate: this is the identifier of the aggregate instance.
     *
     * @return the list of events, in order, for the given aggregate id
     */
    fun <E> loadEvents(aggregate: String, aggregateId: String): List<E>

    /**
     * Loads the events of a given aggregate.
     *
     * @param aggregate the name of the aggregate: i.e: "UserAggregate"; It's case-sensitive.
     * @param aggregateId the id of the aggregate: this is the identifier of the aggregate instance.
     *
     * @param event the event to be stored
     */
    fun save(aggregate: String, aggregateId: String, event: Any)

}