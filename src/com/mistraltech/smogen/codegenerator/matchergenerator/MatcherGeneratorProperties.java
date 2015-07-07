package com.mistraltech.smogen.codegenerator.matchergenerator;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.mistraltech.smogen.codegenerator.CodeWriter;
import com.mistraltech.smogen.codegenerator.JavaGeneratorProperties;

public class MatcherGeneratorProperties extends JavaGeneratorProperties<MatcherGeneratorProperties> {
    private String factoryMethodPrefix;
    private boolean extensible;
    private String matcherSuperClassName;
    private CodeWriter matcherGeneratorCodeWriter;
    private PsiClass sourceClass;
    private String baseClassName;
    private String factoryMethodSuffix;
    private String templateFactoryMethodSuffix;
    private String setterPrefix;
    private String setterSuffix;
    private boolean useReflectingPropertyMatcher;
    private boolean generateTemplateFactoryMethod;
    private boolean makeMethodParametersFinal;
    private boolean generateInterface;

    public MatcherGeneratorProperties() {
    }

    public String getFactoryMethodPrefix() {
        return factoryMethodPrefix;
    }

    public MatcherGeneratorProperties setFactoryMethodPrefix(String factoryMethodPrefix) {
        this.factoryMethodPrefix = factoryMethodPrefix;
        return self();
    }

    public boolean isExtensible() {
        return extensible;
    }

    public MatcherGeneratorProperties setExtensible(boolean extensible) {
        this.extensible = extensible;
        return self();
    }

    public String getMatcherSuperClassName() {
        return matcherSuperClassName;
    }

    public MatcherGeneratorProperties setMatcherSuperClassName(String matcherSuperClassName) {
        this.matcherSuperClassName = matcherSuperClassName;
        return self();
    }

    public PsiClass getSourceClass() {
        return sourceClass;
    }

    public MatcherGeneratorProperties setSourceClass(PsiClass sourceClass) {
        this.sourceClass = sourceClass;
        return self();
    }

    public PsiClassType getSourceSuperClassType() {
        final PsiClassType[] extendsListTypes = sourceClass.getExtendsListTypes();
        if (extendsListTypes.length == 0) {
            return null;
        } else {
            return extendsListTypes[0];
        }
    }

    @Override
    public CodeWriter getCodeWriter() {
        return isGenerateInterface() ? new MatcherInterfaceCodeWriter(this) : new MatcherClassCodeWriter(this);
    }

    public String getBaseClassName() {
        return baseClassName;
    }

    public MatcherGeneratorProperties setBaseClassName(String baseClassName) {
        this.baseClassName = baseClassName;
        return self();
    }

    public String getFactoryMethodSuffix() {
        return factoryMethodSuffix;
    }

    public MatcherGeneratorProperties setFactoryMethodSuffix(String factoryMethodSuffix) {
        this.factoryMethodSuffix = factoryMethodSuffix;
        return self();
    }

    public String getTemplateFactoryMethodSuffix() {
        return templateFactoryMethodSuffix;
    }

    public MatcherGeneratorProperties setTemplateFactoryMethodSuffix(String templateFactoryMethodSuffix) {
        this.templateFactoryMethodSuffix = templateFactoryMethodSuffix;
        return self();
    }

    public String getSetterPrefix() {
        return setterPrefix;
    }

    public MatcherGeneratorProperties setSetterPrefix(String setterPrefix) {
        this.setterPrefix = setterPrefix;
        return self();
    }

    public String getSetterSuffix() {
        return setterSuffix;
    }

    public MatcherGeneratorProperties setSetterSuffix(String setterSuffix) {
        this.setterSuffix = setterSuffix;
        return self();
    }

    public boolean isUseReflectingPropertyMatcher() {
        return useReflectingPropertyMatcher;
    }

    public MatcherGeneratorProperties setUseReflectingPropertyMatcher(boolean useReflectingPropertyMatcher) {
        this.useReflectingPropertyMatcher = useReflectingPropertyMatcher;
        return self();
    }

    public boolean isGenerateTemplateFactoryMethod() {
        return generateTemplateFactoryMethod;
    }

    public MatcherGeneratorProperties setGenerateTemplateFactoryMethod(boolean generateTemplateFactoryMethod) {
        this.generateTemplateFactoryMethod = generateTemplateFactoryMethod;
        return self();
    }

    public boolean isMakeMethodParametersFinal() {
        return makeMethodParametersFinal;
    }

    public MatcherGeneratorProperties setMakeMethodParametersFinal(boolean makeMethodParametersFinal) {
        this.makeMethodParametersFinal = makeMethodParametersFinal;
        return self();
    }

    public boolean isGenerateInterface() {
        return generateInterface;
    }

    public MatcherGeneratorProperties setGenerateInterface(boolean generateInterface) {
        this.generateInterface = generateInterface;
        return self();
    }
}
