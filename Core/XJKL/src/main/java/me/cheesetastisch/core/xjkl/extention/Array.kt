package me.cheesetastisch.core.xjkl.extention

fun Array<out String?>.contains(needle: String, ignoreCase: Boolean = true): Boolean =
    this.any { it.equals(needle, ignoreCase) }

fun String.inArray(haystack: Array<out String?>, ignoreCase: Boolean = true) = haystack.contains(this, ignoreCase)