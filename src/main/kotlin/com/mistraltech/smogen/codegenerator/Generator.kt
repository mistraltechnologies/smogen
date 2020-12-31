package com.mistraltech.smogen.codegenerator

import com.intellij.codeInsight.actions.AbstractLayoutCodeProcessor
import com.intellij.codeInsight.actions.OptimizeImportsProcessor
import com.intellij.codeInsight.actions.RearrangeCodeProcessor
import com.intellij.codeInsight.actions.ReformatCodeProcessor
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.mistraltech.smogen.utils.ActionUtils.runAction
import com.mistraltech.smogen.utils.PsiUtils.createMissingDirectoriesForPackage
import com.mistraltech.smogen.utils.PsiUtils.findDirectoryForPackage

open class Generator(private val generatorProperties: GeneratorProperties<*>) {

    fun generate() {
        val parentDirectory = findOrCreateParentDirectory()
        generatorProperties.setParentDirectory(parentDirectory)

        val existingFile = parentDirectory.findFile(generatorProperties.fileName)
        if (existingFile != null && !shouldOverwriteFile(existingFile)) {
            return
        }

        val warnings = checkProperties()
        if (warnings.isNotEmpty() && !shouldContinueWithWarnings(warnings)) {
            return
        }

        val generatedFile = generateFile()

        reformat(generatedFile)
    }

    protected open fun checkProperties(): List<String> {
        return emptyList()
    }

    private fun generateFile(): PsiFile {
        val fileContentGeneratorRunnable = FileContentGeneratorRunnable(generatorProperties)

        runAction(fileContentGeneratorRunnable)

        return fileContentGeneratorRunnable.targetFile!!
    }

    private fun reformat(targetFile: PsiFile) {
        var processor: AbstractLayoutCodeProcessor = ReformatCodeProcessor(targetFile.project, targetFile, null, false)
        processor = OptimizeImportsProcessor(processor)
        processor = RearrangeCodeProcessor(processor)
        processor.run()
    }

    protected open fun shouldOverwriteFile(existingFile: PsiFile): Boolean {
        return Messages.showYesNoDialog(
            generatorProperties.project,
            "File " + existingFile.virtualFile.presentableUrl + " already exists. Overwrite?",
            "File Already Exists",
            "Overwrite",
            "Cancel",
            Messages.getWarningIcon()
        ) == Messages.YES
    }

    protected open fun shouldContinueWithWarnings(warnings: List<String>): Boolean {
        return Messages.showYesNoDialog(
            generatorProperties.project,
            "The generated source may not compile for the following reasons:\n" +
                (warnings.joinToString("\n")),
            "Generated Code Will Not Compile",
            "Continue",
            "Cancel",
            Messages.getWarningIcon()
        ) == Messages.YES
    }

    private fun findOrCreateParentDirectory(): PsiDirectory {
        val psiManager = PsiManager.getInstance(generatorProperties.project!!)
        val sourceRoot = generatorProperties.sourceRoot
        val baseDir = psiManager.findDirectory(sourceRoot!!)
            ?: throw IllegalStateException("Expected source root directory to exist")

        // Ensure that the directory for the target package exists
        runAction { createMissingDirectoriesForPackage(baseDir, generatorProperties.packageName!!) }

        return findDirectoryForPackage(baseDir, generatorProperties.packageName!!)
            ?: throw IllegalStateException("Expected directory for package to have been created")
    }

    private class FileContentGeneratorRunnable(private val generatorProperties: GeneratorProperties<*>) : Runnable {
        /**
         * Gets the file containing the newly generated matcher class.
         *
         * @return the matcher class file
         */
        var targetFile: PsiFile? = null
            private set

        override fun run() {
            val parentDirectory = generatorProperties.parentDirectory
            val targetFileName = generatorProperties.fileName

            val existingFile = parentDirectory!!.findFile(targetFileName)

            targetFile = existingFile ?: parentDirectory.createFile(targetFileName)

            val documentManager = PsiDocumentManager.getInstance(parentDirectory.project)
            val document = documentManager.getDocument(targetFile!!)
                ?: throw IllegalStateException("Expected document from file " + targetFile!!.name)

            document.setText(generatorProperties.codeWriter.writeCode())
            documentManager.commitDocument(document)
        }
    }
}
