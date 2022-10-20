package me.cheesetastisch.core.bootstrap.util.annotation

import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import me.cheesetastisch.impl.core.bootstrap.util.annotation.IAnnotationScanner

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

}