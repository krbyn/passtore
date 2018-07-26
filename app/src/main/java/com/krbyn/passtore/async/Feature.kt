package com.krbyn.passtore.async

import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class Feature<T> {
    private val result: AtomicReference<T> = AtomicReference()
    private var done: Boolean = false
    private val lock = ReentrantLock()
    private val condition = lock.newCondition()

    fun isDone(): Boolean {
        lock.withLock { return done }
    }

    @Throws(InterruptedException::class)
    fun getValue(): T {
        lock.withLock {
            return if (done) {
                condition.await()
                result.get()
            } else {
                result.get()
            }
        }
    }

    fun setValue(value: T) {
        lock.withLock {
            result.getAndSet(value)
            done = true
            condition.signalAll()
        }
    }

}