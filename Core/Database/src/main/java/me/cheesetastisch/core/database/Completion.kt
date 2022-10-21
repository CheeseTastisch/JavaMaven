package me.cheesetastisch.core.database

import me.cheesetastisch.core.kotlin.scope.asExpr
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@Suppress("unused", "MemberVisibilityCanBePrivate")
open class Completion<T>(protected val future: CompletableFuture<T>) {

    constructor(value: T): this(CompletableFuture()) {
        this.future.complete(value)
    }

    fun getSync(): T = this.future.get()

    fun getSync(timeout: Long, unit: TimeUnit): T = this.future.get(timeout, unit)

    fun getSync(millis: Long): T = this.getSync(millis, TimeUnit.MILLISECONDS)

    fun then(then: (T) -> Unit) = asExpr { this.future.thenAccept(then) }

    fun then(then: () -> Unit) = asExpr { this.future.thenRun(then) }

    fun catch(exception: (Throwable) -> Unit) {
        this.future.exceptionally {
            exception(it)
            null
        }
    }

}

class UnitCompletion(waitFuture: CompletableFuture<*>) : Completion<Unit>(CompletableFuture<Unit>()) {

    init {
        waitFuture.thenRun { this.future.complete(Unit) }
        waitFuture.exceptionally {
            this.future.completeExceptionally(it)
            null
        }
    }

}