package me.cheesetastisch.core.xjkl.future

class ToUnitFuture(waitFuture: java.util.concurrent.CompletableFuture<*>) : CompletableFuture<Unit>() {

    init {
        waitFuture.thenRun { complete(Unit) }
        waitFuture.exceptionally {
            fail(it)
            null
        }
    }

}