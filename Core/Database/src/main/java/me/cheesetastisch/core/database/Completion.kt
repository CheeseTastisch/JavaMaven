package me.cheesetastisch.core.database

import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

open class Completion<T>(protected val future: CompletableFuture<T>) {

    constructor(value: T): this(CompletableFuture()) {
        this.future.complete(value)
    }

    fun getSync(): T = this.future.get()

    fun getSync(timeout: Long, unit: TimeUnit): T = this.future.get(timeout, unit)

    fun getSync(millis: Long): T = this.getSync(millis, TimeUnit.MILLISECONDS)

    fun then(then: (T) -> Unit) = this.future.thenAccept(then)

    fun then(then: () -> Unit) = this.future.thenRun(then)

    fun catch(exception: (Throwable) -> Unit) {
        this.future.exceptionally {
            exception(it)
            null
        }
    }

}

class UnitCompletion(private val waitFuture: CompletableFuture<*>) : Completion<Unit>(CompletableFuture<Unit>()) {

    init {
        waitFuture.thenRun { this.future.complete(Unit) }
        waitFuture.exceptionally {
            this.future.completeExceptionally(it)
            null
        }
    }

}