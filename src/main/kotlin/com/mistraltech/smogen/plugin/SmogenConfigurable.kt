package com.mistraltech.smogen.plugin

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

class SmogenConfigurable(private val project: Project) : Configurable {

    private var smogenConfiguration: SmogenConfiguration? = null

    override fun getDisplayName(): @Nls String {
        return "SMOG Matcher Generator"
    }

    override fun getHelpTopic(): String? {
        return null
    }

    override fun createComponent(): JComponent? {
        smogenConfiguration = SmogenConfiguration(project)
        return smogenConfiguration?.mainPanel
    }

    override fun isModified(): Boolean {
        return smogenConfiguration?.isModified ?: false
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        smogenConfiguration?.save() ?: throw ConfigurationException("No configuration object")
    }

    override fun reset() {
        smogenConfiguration?.reset()
    }

    override fun disposeUIResources() {
        smogenConfiguration = null
    }
}
