package com.github.eslib

import com.github.eslib.api.AggregateRegistry
import com.github.eslib.api.Command
import com.github.eslib.fixture.UserAggregate
import com.github.eslib.infrastructure.InMemoryEventStore
import com.github.eslib.infrastructure.SimpleAggregateCache
import org.junit.Assert
import org.junit.Test

class IntegrationTest {

    @Test
    fun `integrationTest`() {

        /**
         * Those are the three main components of the lib
         *
         * TODO:
         * 1. change factory to have a second argument: the first command
         *
         */
        val eventStore = InMemoryEventStore()
        val registry = AggregateRegistry(eventStore)
        val cache = SimpleAggregateCache(2000)
        val command = Command(registry, eventStore, cache)

        registry.register(name = "UserAggregate",
                          baseCommandClass = UserAggregate.UserCommand::class.java,
                          isNewFunction = { cmd -> cmd is UserAggregate.NewUserCommand },
                          aggregateFactory = { id -> UserAggregate(id=id) },
                          commandHandler = UserAggregate::handleCommand,
                          eventHandler = UserAggregate::handleEvent)

        command.process<Any>("1", UserAggregate.NewUserCommand("ze", "123"))
        val changedEvent: UserAggregate.PasswordChanged =
            command.process("1", UserAggregate.ChangePasswordCommand("newPassword"))
        Assert.assertNotNull(changedEvent)

        val events = eventStore.loadEvents<UserAggregate.UserEvent>("UserAggregate", "1")
        Assert.assertEquals(2, events.size)

    }

}