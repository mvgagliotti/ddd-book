package com.github.eslib.internal

/**
 * Aggregate factory interface
 */
interface AggregateFactory<A> {
    fun create(id: String): A
}