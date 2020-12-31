package com.mistraltech.smogen.plugin

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiClass

interface MatcherGeneratorOptionsPanelDataSource {
    val project: Project
    val recentsKey: String
    val packageName: String
    val matchedClass: PsiClass
    val defaultClassName: String
    val defaultRoot: VirtualFile
    val candidateRoots: List<VirtualFile>
    val defaultIsExtensible: Boolean
}
