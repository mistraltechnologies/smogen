package com.mistraltech.smogen.generator;

import com.intellij.openapi.vfs.VirtualFile;

public class GeneratorProperties {
    private String className;
    private String factoryMethodPrefix;
    private boolean extensible;
    private String concreteSubclassName;
    private String packageName;
    private VirtualFile sourceRoot;
    private String superClassName;

    public GeneratorProperties setClassName(String className) {
        this.className = className;
        return this;
    }

    public String getClassName() {
        return className;
    }

    public GeneratorProperties setFactoryMethodPrefix(String factoryMethodPrefix) {
        this.factoryMethodPrefix = factoryMethodPrefix;
        return this;
    }

    public String getFactoryMethodPrefix() {
        return factoryMethodPrefix;
    }

    public GeneratorProperties setExtensible(boolean extensible) {
        this.extensible = extensible;
        return this;
    }

    public boolean isExtensible() {
        return extensible;
    }

    public GeneratorProperties setConcreteSubclassName(String concreteSubclassName) {
        this.concreteSubclassName = concreteSubclassName;
        return this;
    }

    public String getConcreteSubclassName() {
        return concreteSubclassName;
    }

    public GeneratorProperties setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public String getPackageName() {
        return packageName;
    }

    public GeneratorProperties setSourceRoot(VirtualFile sourceRoot) {
        this.sourceRoot = sourceRoot;
        return this;
    }

    public VirtualFile getSourceRoot() {
        return sourceRoot;
    }

    public GeneratorProperties setSuperClassName(String superClassName) {
        this.superClassName = superClassName;
        return this;
    }

    public String getSuperClassName() {
        return superClassName;
    }
}
