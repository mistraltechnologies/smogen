package com.mistraltech.smogen.codegenerator

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory

abstract class GeneratorProperties<T : GeneratorProperties<T>> {
    var project: Project? = null
        private set
    var packageName: String? = null
        private set
    var sourceRoot: VirtualFile? = null
        private set
    var className: String? = null
        private set
    var parentDirectory: PsiDirectory? = null
        private set

    @Suppress("UNCHECKED_CAST")
    protected fun self(): T {
        return this as T
    }

    fun setClassName(className: String?): T {
        this.className = className
        return self()
    }

    fun setPackageName(packageName: String?): T {
        this.packageName = packageName
        return self()
    }

    fun setSourceRoot(sourceRoot: VirtualFile?): T {
        this.sourceRoot = sourceRoot
        return self()
    }

    fun setParentDirectory(parentDirectory: PsiDirectory?): T {
        this.parentDirectory = parentDirectory
        return self()
    }

    fun setProject(project: Project?): T {
        this.project = project
        return self()
    }

    abstract val fileName: String
    abstract val codeWriter: CodeWriter
}
