package pl.tin.server

import java.util.function.Predicate

fun <T> Iterable<T>.findLast(predicate: Predicate<T>): T? =
    this.findLast { predicate.test(it) }