package com.mistraltech.smogen.plugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiClass
import com.intellij.psi.util.ClassUtil
import com.mistraltech.smogen.codegenerator.matchergenerator.MatcherGenerator
import com.mistraltech.smogen.codegenerator.matchergenerator.MatcherGeneratorProperties
import com.mistraltech.smogen.utils.ActionEventUtils.getTargetFile
import com.mistraltech.smogen.utils.ActionEventUtils.getTargetProject
import com.mistraltech.smogen.utils.ActionEventUtils.hasFile
import com.mistraltech.smogen.utils.ActionEventUtils.hasProject
import com.mistraltech.smogen.utils.ActionEventUtils.hasSelectedClass
import com.mistraltech.smogen.utils.PsiUtils.getSelectedClass
import com.mistraltech.smogen.utils.SourceRootUtils.getPreferredSourceRootForTestClass
import com.mistraltech.smogen.utils.SourceRootUtils.getSourceAndTestSourceRoots

class GenerateMatcherAction : AnAction() {

    override fun update(event: AnActionEvent) {
        val presentation = event.presentation
        presentation.isEnabled = (hasProject(event) && hasFile(event) && hasSelectedClass(event))
        presentation.isVisible = true
    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = getTargetProject(event)
        val selectedFile = getTargetFile(event)
        val selectedClass: PsiClass =
            getSelectedClass(event) ?: throw IllegalStateException("Cannot perform action without a selected class")

        // Note that module may be null if the selected class is in a library rather than the source tree
        val module = event.getData(LangDataKeys.MODULE)
        val configurationProperties = ConfigurationProperties(project)

        // Guess what we are going to call the target matcher class and what its package will be
        val preferredClassName = configurationProperties.matcherClassNamePrefix +
            selectedClass.name +
            configurationProperties.matcherClassNameSuffix
        val preferredPackageName = ClassUtil.extractPackageName(selectedClass.qualifiedName)

        // Get the list of available source roots
        val candidateSourceRoots = getSourceAndTestSourceRoots(project)
        if (candidateSourceRoots.isEmpty()) {
            Messages.showErrorDialog(
                project,
                "No source roots have been configured. " +
                    "A source root is required as the target location for the generated class.",
                "No Source Root"
            )
            return
        }

        // Guess what the target source root might be
        val preferredSourceRoot: VirtualFile =
            getPreferredSourceRootForTestClass(project, module, selectedFile.virtualFile)
                ?: throw IllegalStateException("Cannot find appropriate source root")

        val generatorProperties: MatcherGeneratorProperties = MatcherGeneratorProperties()
            .setProject(project)
            .setClassName(preferredClassName)
            .setPackageName(preferredPackageName)
            .setSourceRoot(preferredSourceRoot)
            .setSourceClass(selectedClass)
            .setFactoryMethodSuffix(configurationProperties.factoryMethodSuffix)
            .setTemplateFactoryMethodSuffix(configurationProperties.templateFactoryMethodSuffix)
            .setSetterPrefix(configurationProperties.setterPrefix)
            .setSetterSuffix(configurationProperties.setterSuffix)
            .setExtensible(configurationProperties.isMakeExtensible)
            .setBaseClassName(configurationProperties.baseClass)
            .setGenerateTemplateFactoryMethod(configurationProperties.isGenerateTemplateFactoryMethod)
            .setMakeMethodParametersFinal(configurationProperties.isMakeMethodParametersFinal)
            .setUseReflectingPropertyMatcher(configurationProperties.isUseReflectingPropertyMatcher)

        val matcherGeneratorOptionsDialog = MatcherGeneratorOptionsDialog(
            project,
            selectedClass,
            candidateSourceRoots,
            generatorProperties
        )

        matcherGeneratorOptionsDialog.show()

        if (matcherGeneratorOptionsDialog.isOK) {
            MatcherGenerator(generatorProperties).generate()
        }
    }
}
