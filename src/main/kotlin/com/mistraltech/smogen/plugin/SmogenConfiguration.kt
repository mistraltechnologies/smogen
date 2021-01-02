package com.mistraltech.smogen.plugin

import com.intellij.openapi.project.Project
import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JTextField

class SmogenConfiguration(private val project: Project) {

    var mainPanel: JPanel? = null

    private var matcherClassNameSuffixTextField: JTextField? = null
    private var matcherClassNamePrefixTextField: JTextField? = null
    private var useReflectingPropertyMatcherCheckBox: JCheckBox? = null
    private var generateTemplateFactoryMethodCheckBox: JCheckBox? = null
    private var makeMethodParametersFinalCheckBox: JCheckBox? = null
    private var baseClassTextField: JTextField? = null
    private var makeExtensibleCheckBox: JCheckBox? = null

    private var currentMatcherClassNamePrefix: String? = null
    private var currentMatcherClassNameSuffix: String? = null
    private var currentFactoryMethodSuffix: String? = null
    private var currentTemplateFactoryMethodSuffix: String? = null
    private var currentSetterPrefix: String? = null
    private var currentSetterSuffix: String? = null
    private var currentUseReflectingPropertyMatcher = false
    private var currentGenerateTemplateFactoryMethod = false
    private var currentMakeMethodParametersFinal = false
    private var currentBaseClass: String? = null
    private var currentMakeExtensible = false

    val isModified: Boolean
        get() = matcherClassNamePrefixTextField!!.text != currentMatcherClassNamePrefix ||
            matcherClassNameSuffixTextField!!.text != currentMatcherClassNameSuffix ||
            useReflectingPropertyMatcherCheckBox!!.isSelected != currentUseReflectingPropertyMatcher ||
            generateTemplateFactoryMethodCheckBox!!.isSelected != currentGenerateTemplateFactoryMethod ||
            makeMethodParametersFinalCheckBox!!.isSelected != currentMakeMethodParametersFinal ||
            baseClassTextField!!.text != currentBaseClass ||
            makeExtensibleCheckBox!!.isSelected != currentMakeExtensible

    private fun init() {
        val properties = ConfigurationProperties(project)

        currentMatcherClassNamePrefix = properties.matcherClassNamePrefix
        currentMatcherClassNameSuffix = properties.matcherClassNameSuffix
        currentFactoryMethodSuffix = properties.factoryMethodSuffix
        currentTemplateFactoryMethodSuffix = properties.templateFactoryMethodSuffix
        currentSetterPrefix = properties.setterPrefix
        currentSetterSuffix = properties.setterSuffix
        currentUseReflectingPropertyMatcher = properties.isUseReflectingPropertyMatcher
        currentGenerateTemplateFactoryMethod = properties.isGenerateTemplateFactoryMethod
        currentMakeMethodParametersFinal = properties.isMakeMethodParametersFinal
        currentBaseClass = properties.baseClass
        currentMakeExtensible = properties.isMakeExtensible

        reset()
    }

    fun save() {
        val properties = ConfigurationProperties(project)

        currentMatcherClassNamePrefix = matcherClassNamePrefixTextField!!.text
        currentMatcherClassNameSuffix = matcherClassNameSuffixTextField!!.text
        currentUseReflectingPropertyMatcher = useReflectingPropertyMatcherCheckBox!!.isSelected
        currentGenerateTemplateFactoryMethod = generateTemplateFactoryMethodCheckBox!!.isSelected
        currentMakeMethodParametersFinal = makeMethodParametersFinalCheckBox!!.isSelected
        currentBaseClass = baseClassTextField!!.text
        currentMakeExtensible = makeExtensibleCheckBox!!.isSelected

        properties.matcherClassNamePrefix = currentMatcherClassNamePrefix
        properties.matcherClassNameSuffix = currentMatcherClassNameSuffix
        properties.factoryMethodSuffix = currentFactoryMethodSuffix
        properties.templateFactoryMethodSuffix = currentTemplateFactoryMethodSuffix
        properties.setterPrefix = currentSetterPrefix
        properties.setterSuffix = currentSetterSuffix
        properties.isUseReflectingPropertyMatcher = currentUseReflectingPropertyMatcher
        properties.isGenerateTemplateFactoryMethod = currentGenerateTemplateFactoryMethod
        properties.isMakeMethodParametersFinal = currentMakeMethodParametersFinal
        properties.baseClass = currentBaseClass
        properties.isMakeExtensible = currentMakeExtensible
    }

    fun reset() {
        matcherClassNamePrefixTextField!!.text = currentMatcherClassNamePrefix
        matcherClassNameSuffixTextField!!.text = currentMatcherClassNameSuffix
        useReflectingPropertyMatcherCheckBox!!.isSelected = currentUseReflectingPropertyMatcher
        generateTemplateFactoryMethodCheckBox!!.isSelected = currentGenerateTemplateFactoryMethod
        makeMethodParametersFinalCheckBox!!.isSelected = currentMakeMethodParametersFinal
        baseClassTextField!!.text = currentBaseClass
        makeExtensibleCheckBox!!.isSelected = currentMakeExtensible
    }

    init {
        init()
    }
}
