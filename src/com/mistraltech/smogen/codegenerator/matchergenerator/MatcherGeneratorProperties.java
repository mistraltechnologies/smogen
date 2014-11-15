package com.mistraltech.smogen.codegenerator.matchergenerator;

import com.intellij.psi.PsiClass;
import com.mistraltech.smogen.codegenerator.CodeWriter;
import com.mistraltech.smogen.codegenerator.JavaGeneratorProperties;

public class MatcherGeneratorProperties extends JavaGeneratorProperties<MatcherGeneratorProperties> {
    private String factoryMethodPrefix;
    private boolean extensible;
    private String superClassName;
    private CodeWriter matcherGeneratorCodeWriter;
    private PsiClass sourceClass;

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

    public String getSuperClassName() {
        return superClassName;
    }

    public MatcherGeneratorProperties setSuperClassName(String superClassName) {
        this.superClassName = superClassName;
        return self();
    }

    public PsiClass getSourceClass() {
        return sourceClass;
    }

    public MatcherGeneratorProperties setSourceClass(PsiClass sourceClass) {
        this.sourceClass = sourceClass;
        return self();
    }

    @Override
    public CodeWriter getCodeWriter() {
        if (matcherGeneratorCodeWriter == null) {
            matcherGeneratorCodeWriter = new MatcherGeneratorCodeWriter(this);
        }

        return matcherGeneratorCodeWriter;
    }
}
