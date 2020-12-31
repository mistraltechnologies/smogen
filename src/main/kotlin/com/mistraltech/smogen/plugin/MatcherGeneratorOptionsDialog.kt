package com.mistraltech.smogen.plugin

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiClass
import com.mistraltech.smogen.codegenerator.matchergenerator.MatcherGeneratorProperties
import javax.swing.JComponent

class MatcherGeneratorOptionsDialog(
    override val project: Project,
    override val matchedClass: PsiClass,
    override val candidateRoots: List<VirtualFile>,
    private val generatorProperties: MatcherGeneratorProperties
) : DialogWrapper(project), MatcherGeneratorOptionsPanelDataSource {

    override val recentsKey: String = "MatcherGeneratorOptionsDialog.RecentsKey"

    private val matcherGeneratorOptionsPanel: MatcherGeneratorOptionsPanel

    override fun doOKAction() {
        super.doOKAction()

        val sourceRoot = matcherGeneratorOptionsPanel.selectedSourceRoot

        generatorProperties.setClassName(matcherGeneratorOptionsPanel.selectedClassName)
            .setFactoryMethodPrefix(if (matcherGeneratorOptionsPanel.isAn) "an" else "a")
            .setExtensible(matcherGeneratorOptionsPanel.isMakeExtensible)
            .setMatcherSuperClassName(matcherGeneratorOptionsPanel.superClassName)
            .setPackageName(matcherGeneratorOptionsPanel.selectedPackageName)
            .setGenerateInterface(matcherGeneratorOptionsPanel.isGenerateInterface)
            .setSourceRoot(sourceRoot)
    }

    override fun doValidate(): ValidationInfo? {
        val validationInfo = matcherGeneratorOptionsPanel.doValidate()
        return validationInfo ?: super.doValidate()
    }

    override fun createCenterPanel(): JComponent {
        return matcherGeneratorOptionsPanel.root
    }

    override val packageName: String
        get() = generatorProperties.packageName!!
    override val defaultClassName: String
        get() = generatorProperties.className!!
    override val defaultRoot: VirtualFile
        get() = generatorProperties.sourceRoot!!
    override val defaultIsExtensible: Boolean
        get() = generatorProperties.isExtensible

    init {
        isModal = true
        title = "Generate Matcher"
        matcherGeneratorOptionsPanel = MatcherGeneratorOptionsPanel(this)
        init()
    }
}
