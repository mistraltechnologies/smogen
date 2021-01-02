@file:Suppress("MemberVisibilityCanBePrivate")

package com.mistraltech.smogen.utils

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.roots.SourceFolder
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.jps.model.java.JavaSourceRootProperties
import org.jetbrains.jps.model.java.JavaSourceRootType

object SourceRootUtils {
    /**
     * Gets the source and test source root directories for a project.
     *
     * @param project the project
     * @return list of source and test source root directories
     */
    @JvmStatic
    fun getSourceAndTestSourceRoots(project: Project): List<VirtualFile> {
        return getSourceRoots(project, JavaSourceRootType.SOURCE, JavaSourceRootType.TEST_SOURCE)
    }

    /**
     * Gets the source root directories (excluding test source root directories) for a project.
     *
     * @param project the project
     * @return list of source root directories
     */
    fun getSourceRoots(project: Project): List<VirtualFile> {
        return getSourceRoots(project, JavaSourceRootType.SOURCE)
    }

    /**
     * Gets the test source root directories for a project.
     *
     * @param project the project
     * @return list of test source root directories
     */
    fun getTestSourceRoots(project: Project): List<VirtualFile> {
        return getSourceRoots(project, JavaSourceRootType.TEST_SOURCE)
    }

    /**
     * Gets the source root directories (excluding test source root directories) for a module.
     *
     * @param module the module
     * @return list of source root directories
     */
    fun getSourceRoots(module: Module): List<VirtualFile> {
        return getSourceRoots(module, JavaSourceRootType.SOURCE)
    }

    /**
     * Gets the test source root directories for a module.
     *
     * @param module the module
     * @return list of test source root directories
     */
    fun getTestSourceRoots(module: Module): List<VirtualFile> {
        return getSourceRoots(module, JavaSourceRootType.TEST_SOURCE)
    }

    private fun getSourceRoots(project: Project, vararg rootTypes: JavaSourceRootType): List<VirtualFile> {
        return getSourceRoots(project, rootTypes.toSet())
    }

    private fun getSourceRoots(project: Project, rootTypes: Set<JavaSourceRootType>): List<VirtualFile> {
        val modules: Array<Module> = ModuleManager.getInstance(project).modules
        return modules.flatMap { module -> getSourceRoots(module, rootTypes) }
    }

    private fun getSourceRoots(module: Module, vararg rootTypes: JavaSourceRootType): List<VirtualFile> {
        return getSourceRoots(module, rootTypes.toSet())
    }

    private fun getSourceRoots(module: Module, rootTypes: Set<JavaSourceRootType>): List<VirtualFile> {
        val contentEntries = ModuleRootManager.getInstance(module).contentEntries

        return contentEntries.flatMap { contentEntry -> contentEntry.getSourceFolders(rootTypes) }
            .filter { sourceFolder -> !isForGeneratedSources(sourceFolder) }
            .mapNotNull { sourceFolder -> sourceFolder.file }
    }

    private fun isForGeneratedSources(sourceFolder: SourceFolder): Boolean {
        val rootType: JavaSourceRootType = sourceFolder.rootType as JavaSourceRootType
        val properties: JavaSourceRootProperties? = sourceFolder.jpsElement.getProperties(rootType)
        return properties != null && properties.isForGeneratedSources
    }

    /**
     * Gets the most suitable source root from those available.
     *
     *
     * Returns the first matching case:
     *
     *  1. A current module is specified, that module has test roots and the selected file belongs to one of those
     *  test roots => that test root
     *  2. A current module is specified and that module has test roots => the first test root in the module
     *  3. A current module is specified, that module has source roots and the selected file belongs to one of those
     *  source roots => that source root
     *  4. A current module is specified and that module has source roots = the first source root in the module
     *  5. The project has test roots => the first test root
     *  6. The project has source roots => the first source root
     *  7. Otherwise returns null
     *
     *
     * @param project the project
     * @param module optional current module - will be null if a library class is selected
     * @param selectedFile the currently selected file
     * @return the preferred source root if one exists; otherwise null
     */
    @JvmStatic
    fun getPreferredSourceRootForTestClass(
        project: Project,
        module: Module?,
        selectedFile: VirtualFile?
    ): VirtualFile? {
        val candidateSourceRoots: List<VirtualFile> = getCandidatePreferredSourceRoots(project, module)

        val currentSourceRoot: VirtualFile? by lazy {
            if (selectedFile != null) getFileIndex(project).getSourceRootForFile(selectedFile) else null
        }

        return if (candidateSourceRoots.isEmpty()) null
        else if (currentSourceRoot != null && candidateSourceRoots.contains(currentSourceRoot)) currentSourceRoot
        else candidateSourceRoots[0]
    }

    private fun getFileIndex(project: Project): ProjectFileIndex {
        return ProjectRootManager.getInstance(project).fileIndex
    }

    private fun getCandidatePreferredSourceRoots(project: Project, module: Module?): List<VirtualFile> {
        val validTestSourceRoots: List<VirtualFile> =
            if (module != null) getTestSourceRoots(module) else getTestSourceRoots(project)

        val validSourceRoots: List<VirtualFile> by lazy {
            if (module != null) getSourceRoots(module) else getSourceRoots(project)
        }

        return if (validTestSourceRoots.isNotEmpty()) validTestSourceRoots else validSourceRoots
    }
}
