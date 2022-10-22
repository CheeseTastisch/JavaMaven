package me.cheesetastisch.core.xjkl.future

import me.cheesetastisch.core.xjkl.scope.asExpr
import java.util.concurrent.CompletableFuture

open class CompletableFuture<T> : Future<T>(CompletableFuture<T>()) {

    fun complete(value: T) = asExpr { this.future.complete(value) }

    fun fail(e: Throwable) = asExpr { this.future.completeExceptionally(e) }

}