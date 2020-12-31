package com.mistraltech.smogen.plugin

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project

class ConfigurationProperties(project: Project) {
    private val properties: PropertiesComponent = PropertiesComponent.getInstance(project)

    var matcherClassNamePrefix: String?
        get() = properties.getValue(MATCHER_CLASS_NAME_PREFIX, MATCHER_CLASS_NAME_PREFIX_DEFAULT)
        set(prefix) {
            properties.setValue(MATCHER_CLASS_NAME_PREFIX, prefix, MATCHER_CLASS_NAME_PREFIX_DEFAULT)
        }

    var matcherClassNameSuffix: String?
        get() = properties.getValue(MATCHER_CLASS_NAME_SUFFIX, MATCHER_CLASS_NAME_SUFFIX_DEFAULT)
        set(suffix) {
            properties.setValue(MATCHER_CLASS_NAME_SUFFIX, suffix, MATCHER_CLASS_NAME_SUFFIX_DEFAULT)
        }

    var factoryMethodSuffix: String?
        get() = properties.getValue(FACTORY_METHOD_SUFFIX, FACTORY_METHOD_SUFFIX_DEFAULT)
        set(suffix) {
            properties.setValue(FACTORY_METHOD_SUFFIX, suffix, FACTORY_METHOD_SUFFIX_DEFAULT)
        }

    var templateFactoryMethodSuffix: String?
        get() = properties.getValue(TEMPLATE_FACTORY_METHOD_SUFFIX, TEMPLATE_FACTORY_METHOD_SUFFIX_DEFAULT)
        set(suffix) {
            properties.setValue(TEMPLATE_FACTORY_METHOD_SUFFIX, suffix, TEMPLATE_FACTORY_METHOD_SUFFIX_DEFAULT)
        }

    var setterPrefix: String?
        get() = properties.getValue(SETTER_PREFIX, SETTER_PREFIX_DEFAULT)
        set(prefix) {
            properties.setValue(SETTER_PREFIX, prefix, SETTER_PREFIX_DEFAULT)
        }

    var setterSuffix: String?
        get() = properties.getValue(SETTER_SUFFIX, SETTER_SUFFIX_DEFAULT)
        set(suffix) {
            properties.setValue(SETTER_SUFFIX, suffix, SETTER_SUFFIX_DEFAULT)
        }

    var isUseReflectingPropertyMatcher: Boolean
        get() {
            return properties.getValue(USE_REFLECTING_PROPERTY_MATCHER, USE_REFLECTING_PROPERTY_MATCHER_DEFAULT)
                .toBoolean()
        }
        set(useReflectingPropertyMatcherExtensible) {
            properties.setValue(
                USE_REFLECTING_PROPERTY_MATCHER,
                useReflectingPropertyMatcherExtensible,
                USE_REFLECTING_PROPERTY_MATCHER_DEFAULT.toBoolean()
            )
        }

    var isGenerateTemplateFactoryMethod: Boolean
        get() {
            return properties.getValue(GENERATE_TEMPLATE_FACTORY_METHOD, GENERATE_TEMPLATE_FACTORY_METHOD_DEFAULT)
                .toBoolean()
        }
        set(generateTemplateFactoryMethod) {
            properties.setValue(
                GENERATE_TEMPLATE_FACTORY_METHOD,
                generateTemplateFactoryMethod,
                GENERATE_TEMPLATE_FACTORY_METHOD_DEFAULT.toBoolean()
            )
        }

    var isMakeMethodParametersFinal: Boolean
        get() {
            return properties.getValue(MAKE_METHOD_PARAMETERS_FINAL, MAKE_METHOD_PARAMETERS_FINAL_DEFAULT).toBoolean()
        }
        set(makeMethodParametersFinal) {
            properties.setValue(
                MAKE_METHOD_PARAMETERS_FINAL,
                makeMethodParametersFinal,
                MAKE_METHOD_PARAMETERS_FINAL_DEFAULT.toBoolean()
            )
        }

    var baseClass: String?
        get() = properties.getValue(BASE_CLASS, BASE_CLASS_DEFAULT)
        set(baseClass) {
            properties.setValue(BASE_CLASS, baseClass, BASE_CLASS_DEFAULT)
        }

    var isMakeExtensible: Boolean
        get() {
            return properties.getValue(MAKE_EXTENSIBLE, MAKE_EXTENSIBLE_DEFAULT).toBoolean()
        }
        set(makeExtensible) {
            properties.setValue(MAKE_EXTENSIBLE, makeExtensible, MAKE_EXTENSIBLE_DEFAULT.toBoolean())
        }

    @Suppress("unused")
    fun reset() {
        matcherClassNamePrefix = MATCHER_CLASS_NAME_PREFIX_DEFAULT
        matcherClassNameSuffix = MATCHER_CLASS_NAME_SUFFIX_DEFAULT
        factoryMethodSuffix = FACTORY_METHOD_SUFFIX_DEFAULT
        templateFactoryMethodSuffix = TEMPLATE_FACTORY_METHOD_SUFFIX_DEFAULT
        setterPrefix = SETTER_PREFIX_DEFAULT
        setterSuffix = SETTER_SUFFIX_DEFAULT
        isUseReflectingPropertyMatcher = USE_REFLECTING_PROPERTY_MATCHER_DEFAULT.toBoolean()
        isGenerateTemplateFactoryMethod = GENERATE_TEMPLATE_FACTORY_METHOD_DEFAULT.toBoolean()
        isMakeMethodParametersFinal = MAKE_METHOD_PARAMETERS_FINAL_DEFAULT.toBoolean()
        baseClass = BASE_CLASS_DEFAULT
        isMakeExtensible = MAKE_EXTENSIBLE_DEFAULT.toBoolean()
    }

    companion object {
        private const val MATCHER_CLASS_NAME_PREFIX = "smogen.matcherPrefix"
        private const val MATCHER_CLASS_NAME_PREFIX_DEFAULT = ""
        private const val MATCHER_CLASS_NAME_SUFFIX = "smogen.matcherSuffix"
        private const val MATCHER_CLASS_NAME_SUFFIX_DEFAULT = "Matcher"
        private const val FACTORY_METHOD_SUFFIX = "smogen.factoryMethodSuffix"
        private const val FACTORY_METHOD_SUFFIX_DEFAULT = "That"
        private const val TEMPLATE_FACTORY_METHOD_SUFFIX = "smogen.templateFactoryMethodSuffix"
        private const val TEMPLATE_FACTORY_METHOD_SUFFIX_DEFAULT = "Like"
        private const val SETTER_PREFIX = "smogen.setterPrefix"
        private const val SETTER_PREFIX_DEFAULT = "has"
        private const val SETTER_SUFFIX = "smogen.setterSuffix"
        private const val SETTER_SUFFIX_DEFAULT = ""
        private const val USE_REFLECTING_PROPERTY_MATCHER = "smogen.useReflectingPropertyMatcher"
        private const val USE_REFLECTING_PROPERTY_MATCHER_DEFAULT = "true"
        private const val GENERATE_TEMPLATE_FACTORY_METHOD = "smogen.generateTemplateFactoryMethod"
        private const val GENERATE_TEMPLATE_FACTORY_METHOD_DEFAULT = "true"
        private const val MAKE_METHOD_PARAMETERS_FINAL = "smogen.makeMethodParametersFinal"
        private const val MAKE_METHOD_PARAMETERS_FINAL_DEFAULT = "true"
        private const val BASE_CLASS = "smogen.baseClass"
        private const val BASE_CLASS_DEFAULT = "com.mistraltech.smog.core.CompositePropertyMatcher"
        private const val MAKE_EXTENSIBLE = "smogen.makeExtensible"
        private const val MAKE_EXTENSIBLE_DEFAULT = "false"
    }
}
