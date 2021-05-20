package com.github.eslib.api

interface AggregateCache {
    fun put(key: Pair<String, String>, value: Any)
    fun get(key: Pair<String, String>): Any?
    fun finish()
}