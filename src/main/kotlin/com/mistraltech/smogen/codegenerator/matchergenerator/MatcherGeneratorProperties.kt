package com.mistraltech.smogen.codegenerator.matchergenerator

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassType
import com.mistraltech.smogen.codegenerator.CodeWriter
import com.mistraltech.smogen.codegenerator.JavaGeneratorProperties

@Suppress("TooManyFunctions")
class MatcherGeneratorProperties : JavaGeneratorProperties<MatcherGeneratorProperties>() {
    var factoryMethodPrefix: String? = null
        private set
    var isExtensible = false
        private set
    var matcherSuperClassName: String? = null
        private set
    var sourceClass: PsiClass? = null
        private set
    var baseClassName: String? = null
        private set
    var factoryMethodSuffix: String? = null
        private set
    var templateFactoryMethodSuffix: String? = null
        private set
    var setterPrefix: String? = null
        private set
    var setterSuffix: String? = null
        private set
    var isUseReflectingPropertyMatcher = false
        private set
    var isGenerateTemplateFactoryMethod = false
        private set
    var isMakeMethodParametersFinal = false
        private set
    var isGenerateInterface = false
        private set

    fun setFactoryMethodPrefix(factoryMethodPrefix: String?): MatcherGeneratorProperties {
        this.factoryMethodPrefix = factoryMethodPrefix
        return self()
    }

    fun setExtensible(extensible: Boolean): MatcherGeneratorProperties {
        isExtensible = extensible
        return self()
    }

    fun setMatcherSuperClassName(matcherSuperClassName: String?): MatcherGeneratorProperties {
        this.matcherSuperClassName = matcherSuperClassName
        return self()
    }

    fun setSourceClass(sourceClass: PsiClass?): MatcherGeneratorProperties {
        this.sourceClass = sourceClass
        return self()
    }

    fun setBaseClassName(baseClassName: String?): MatcherGeneratorProperties {
        this.baseClassName = baseClassName
        return self()
    }

    fun setFactoryMethodSuffix(factoryMethodSuffix: String?): MatcherGeneratorProperties {
        this.factoryMethodSuffix = factoryMethodSuffix
        return self()
    }

    fun setTemplateFactoryMethodSuffix(templateFactoryMethodSuffix: String?): MatcherGeneratorProperties {
        this.templateFactoryMethodSuffix = templateFactoryMethodSuffix
        return self()
    }

    fun setSetterPrefix(setterPrefix: String?): MatcherGeneratorProperties {
        this.setterPrefix = setterPrefix
        return self()
    }

    fun setSetterSuffix(setterSuffix: String?): MatcherGeneratorProperties {
        this.setterSuffix = setterSuffix
        return self()
    }

    fun setUseReflectingPropertyMatcher(useReflectingPropertyMatcher: Boolean): MatcherGeneratorProperties {
        isUseReflectingPropertyMatcher = useReflectingPropertyMatcher
        return self()
    }

    fun setGenerateTemplateFactoryMethod(generateTemplateFactoryMethod: Boolean): MatcherGeneratorProperties {
        isGenerateTemplateFactoryMethod = generateTemplateFactoryMethod
        return self()
    }

    fun setMakeMethodParametersFinal(makeMethodParametersFinal: Boolean): MatcherGeneratorProperties {
        isMakeMethodParametersFinal = makeMethodParametersFinal
        return self()
    }

    fun setGenerateInterface(generateInterface: Boolean): MatcherGeneratorProperties {
        isGenerateInterface = generateInterface
        return self()
    }

    val sourceSuperClassType: PsiClassType?
        get() {
            return sourceClass?.extendsListTypes?.elementAtOrNull(0)
        }

    override val codeWriter: CodeWriter
        get() = if (isGenerateInterface) MatcherInterfaceCodeWriter(this) else MatcherClassCodeWriter(this)
}
