package me.cheesetastisch.impl.core.bootstrap.util.scope

fun asExpr(statement: () -> Unit) {
    statement()
}

fun <T: Any?, R: Any?> supply(supplied: T, supplier: (T) -> R): R = supplier(supplied)

fun <T: Any, R: Any?> supplyNullable(supplied: T?, supplier: (T) -> R): R? {
    if (supplied == null) return null
    return supplier(supplied)
}

fun <T : Any> T.withThis(statement: () -> Unit): T {
    statement()
    return this
}