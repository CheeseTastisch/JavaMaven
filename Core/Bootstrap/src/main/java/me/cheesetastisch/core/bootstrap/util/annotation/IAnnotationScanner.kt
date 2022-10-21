package me.cheesetastisch.core.bootstrap.util.annotation

import io.github.classgraph.ClassInfo
import kotlin.reflect.KClass

@Suppress("unused")
interface IAnnotationScanner {

    fun scanClasses(
        annotation: KClass<out Annotation>,
        filter: (ClassInfo) -> Boolean,
        vararg packages: String
    ) = scanClasses(annotation.java, filter, *packages)
        .map { it.kotlin }

    fun scanClasses(
        annotation: Class<out Annotation>,
        filter: (ClassInfo) -> Boolean,
        vararg packages: String
    ): List<Class<*>>

    fun scanClasses(
        annotation: KClass<out Annotation>,
        vararg packages: String
    ) = scanClasses(annotation.java, *packages)
        .map { it.kotlin }

    fun scanClasses(
        annotation: Class<out Annotation>,
        vararg packages: String
    ): List<Class<*>> = scanClasses(annotation, { true }, *packages)

}