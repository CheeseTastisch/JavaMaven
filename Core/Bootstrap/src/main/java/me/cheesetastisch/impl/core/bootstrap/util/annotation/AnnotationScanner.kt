package me.cheesetastisch.impl.core.bootstrap.util.annotation

import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import io.github.classgraph.MethodInfo
import me.cheesetastisch.core.bootstrap.util.annotation.IAnnotationScanner

class AnnotationScanner : IAnnotationScanner {

    override fun scanClasses(
        annotation: Class<out Annotation>,
        filter: (ClassInfo) -> Boolean,
        vararg packages: String
    ) = ClassGraph()
        .enableClassInfo()
        .enableAnnotationInfo()
        .acceptPackages(*packages)
        .scan()
        .allClasses
        .filter { it.hasAnnotation(annotation) }
        .filter(filter)
        .map { it.loadClass() }

    override fun scanMethods(
        annotation: Class<out Annotation>,
        filter: (MethodInfo) -> Boolean,
        vararg packages: String
    ) = ClassGraph()
        .enableMethodInfo()
        .enableAnnotationInfo()
        .acceptPackages(*packages)
        .scan()
        .allClasses
        .asSequence()
        .map { it.methodInfo }
        .flatten()
        .filter { it.hasAnnotation(annotation) }
        .filter(filter)
        .map { it.loadClassAndGetMethod() }
        .toList()

}