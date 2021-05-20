package com.github.eslib.fixture

import java.lang.IllegalArgumentException

/**
 * Test Aggregate
 */
data class UserAggregate(val id: String,
                         val username: String = "",
                         val password: String = "") {

    interface UserCommand;
    interface UserEvent
    data class NewUserCommand(val username: String, val password: String) : UserCommand
    data class ChangePasswordCommand(val newPassword: String) : UserCommand
    data class NewUserCreated(val id: String, val username: String, val password: String) : UserEvent
    data class PasswordChanged(val newPassword: String) : UserEvent

    fun handleCommand(cmd: UserCommand): UserEvent = when (cmd) {
        is NewUserCommand -> {
            NewUserCreated(id = this.id, username = cmd.username, password = cmd.password)
        }
        is ChangePasswordCommand -> {
            PasswordChanged(newPassword = cmd.newPassword)
        }
        else -> {
            throw IllegalArgumentException("Unknown command:  $cmd")
        }
    }

    fun handleEvent(evt: UserEvent) = when (evt) {
        is NewUserCreated -> {
            this.copy(username = evt.username, password = evt.password)
        }
        is PasswordChanged -> {
            this.copy(password = evt.newPassword)
        }
        else -> {
            throw IllegalArgumentException("WTF??")
        }
    }
}