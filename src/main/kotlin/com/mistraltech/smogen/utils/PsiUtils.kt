package com.mistraltech.smogen.utils

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiAnonymousClass
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiModifier
import com.intellij.psi.PsiTypeParameter
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiUtilBase

@Suppress("unused")
object PsiUtils {
    /**
     * For a given element, determines the class element that is the the nearest
     * enclosing class element, excluding type parameters and anonymous inner classes.
     *
     * @param psiElement the input element
     * @return the nearest enclosing qualifying class element, or null
     */
    @JvmStatic
    fun findClassFromElement(psiElement: PsiElement): PsiClass? {
        var containingClass: PsiClass? = PsiTreeUtil.getParentOfType(psiElement, PsiClass::class.java, false)

        while (containingClass is PsiTypeParameter || containingClass is PsiAnonymousClass) {
            containingClass = PsiTreeUtil.getParentOfType(containingClass, PsiClass::class.java)
        }

        return containingClass
    }

    /**
     * For a given file element, gets the enclosed class that is publicly accessible.
     *
     * @param file the file
     * @return the enclosed public class if found
     */
    @JvmStatic
    fun getPublicClassFromFile(file: PsiFile): PsiClass? {
        val javaFile: PsiJavaFile? = if (file is PsiJavaFile) file else null

        val childClasses: List<PsiClass>? = javaFile?.children?.filterIsInstance<PsiClass>()

        return childClasses
            ?.filter { psiClass -> psiClass.hasModifierProperty("public") }
            ?.getOrNull(0)
    }

    /**
     * Searching from the supplied base directory, finds the directory that represents a given package name.
     *
     * @param baseDir the base directory
     * @param packageName the package name
     * @return the directory relating to the package, or null if the directory does not exist
     */
    @JvmStatic
    fun findDirectoryForPackage(baseDir: PsiDirectory, packageName: String): PsiDirectory? {
        return processPackagesOptional(
            baseDir,
            { dir: PsiDirectory, name: String -> dir.findSubdirectory(name) },
            getPackageAsComponents(packageName)
        )
    }

    /**
     * For a given base directory and package name, ensures that the related package directory exists.
     *
     * @param baseDir the base directory
     * @param packageName the package name
     * @return the directory relating to the package
     */
    @JvmStatic
    fun createMissingDirectoriesForPackage(baseDir: PsiDirectory, packageName: String): PsiDirectory {
        return processPackages(
            baseDir,
            { dir: PsiDirectory, name: String -> findOrCreateSubdirectory(dir, name) },
            getPackageAsComponents(packageName)
        )
    }

    private fun findOrCreateSubdirectory(baseDir: PsiDirectory, name: String): PsiDirectory {
        return baseDir.findSubdirectory(name) ?: baseDir.createSubdirectory(name)
    }

    private fun processPackages(
        baseDir: PsiDirectory,
        function: (baseDir: PsiDirectory, packageName: String) -> PsiDirectory,
        packageNames: List<String>
    ): PsiDirectory {

        if (packageNames.isEmpty()) {
            return baseDir
        }

        val packageNamesHead = packageNames[0]
        val packageNamesTail = packageNames.subList(1, packageNames.size)
        val subdirectory: PsiDirectory = function(baseDir, packageNamesHead)
        return processPackages(subdirectory, function, packageNamesTail)
    }

    private fun processPackagesOptional(
        baseDir: PsiDirectory,
        function: (baseDir: PsiDirectory, packageName: String) -> PsiDirectory?,
        packageNames: List<String>
    ): PsiDirectory? {

        if (packageNames.isEmpty()) {
            return baseDir
        }

        val packageNamesHead = packageNames[0]
        val packageNamesTail = packageNames.subList(1, packageNames.size)
        val subdirectory: PsiDirectory? = function(baseDir, packageNamesHead)
        return subdirectory?.let { processPackagesOptional(it, function, packageNamesTail) }
    }

    private fun getPackageAsComponents(packageName: String): List<String> {
        return if (packageName.isEmpty()) emptyList() else packageName.split("\\.".toRegex())
    }

    /**
     * Determine if the supplied class is abstract.
     *
     * @param psiClass the class
     * @return true if psiClass is abstract; false otherwise
     */
    @JvmStatic
    fun isAbstract(psiClass: PsiClass): Boolean {
        return psiClass.hasModifierProperty(PsiModifier.ABSTRACT)
    }

    /**
     * Gets the selected class for the event if available.
     *
     * If there is a current editor and the caret is over a class, uses that class.
     * Else if there is a selected element in the data context that can be mapped to a class, uses that class.
     * Else attempts to get a public class from the current selected file.
     *
     * @param event an event that has been triggered
     * @return the most appropriate class selection for that event, or null
     */
    @JvmStatic
    fun getSelectedClass(event: AnActionEvent): PsiClass? {
        val file: PsiFile = ActionEventUtils.getTargetFile(event)
        val project: Project = ActionEventUtils.getTargetProject(event)
        val editor: Editor? = ActionEventUtils.getOptionalTargetEditor(event)

        var targetClass: PsiClass? = editor?.let { getSelectedClassFromEditor(project, editor) }

        if (targetClass == null) {
            targetClass = getSelectedElement(event.dataContext)?.let { element ->
                findClassFromElement(element)
            }
        }

        if (targetClass == null) {
            targetClass = getPublicClassFromFile(file)
        }

        return targetClass
    }

    /**
     * Gets the class that wraps the element under the caret in the editor, if available.
     *
     * @param project the project
     * @param editor the editor
     * @return the class containing the element under the caret, or null
     */
    private fun getSelectedClassFromEditor(project: Project, editor: Editor): PsiClass? {
        val currentElementInEditor = getCurrentElementInEditor(project, editor)
        return currentElementInEditor?.let { findClassFromElement(currentElementInEditor) }
    }

    /**
     * Gets the element under the caret of the given editor, if available.
     *
     * @param project the project
     * @param editor the editor
     * @return the element under the caret, or null
     */
    private fun getCurrentElementInEditor(project: Project, editor: Editor): PsiElement? {
        return PsiUtilBase.getPsiFileInEditor(editor, project)?.findElementAt(editor.caretModel.offset)
    }

    /**
     * Gets the currently selected element, if available.
     *
     * @param dataContext data context
     * @return the currently selected element, or null
     */
    private fun getSelectedElement(dataContext: DataContext): PsiElement? {
        return CommonDataKeys.PSI_ELEMENT.getData(dataContext)
    }
}
