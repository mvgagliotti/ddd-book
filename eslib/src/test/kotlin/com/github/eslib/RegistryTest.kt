package com.github.eslib

import com.github.eslib.api.AggregateRegistry
import com.github.eslib.fixture.UserAggregate
import com.github.eslib.fixture.UserAggregate.*
import com.github.eslib.infrastructure.InMemoryEventStore
import com.github.eslib.internal.AggregateFactory
import org.junit.Assert
import org.junit.Test

class RegistryTest {

    @Test
    fun `given an aggregate is registered it should be able to handle commands`() {
        val registry = AggregateRegistry(InMemoryEventStore())

        registry.register(name = "UserAggregate",
                          baseCommandClass = UserCommand::class.java,
                          isNewFunction = { cmd -> cmd is NewUserCommand },
                          aggregateFactory = object : AggregateFactory<UserAggregate> {
                              override fun create(id: String): UserAggregate = UserAggregate(id = id)
                          },
                          commandHandler = UserAggregate::handleCommand,
                          eventHandler = UserAggregate::handleEvent
        )

        val aggregate = registry.lookupByCommand<Any, Any, Any>(NewUserCommand(username = "user",
                                                                               password = "passwd"))

        Assert.assertEquals("UserAggregate", aggregate.name)
    }

}