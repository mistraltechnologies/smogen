package com.mistraltech.smogen.codegenerator

abstract class JavaGeneratorProperties<T : JavaGeneratorProperties<T>> : GeneratorProperties<T>() {
    override val fileName: String
        get() = "$className.java"
}
