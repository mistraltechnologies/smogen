package com.mistraltech.smogen.codegenerator;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;

public abstract class GeneratorProperties<T extends GeneratorProperties> {
    private Project project;
    private String packageName;
    private VirtualFile sourceRoot;
    private String className;
    private PsiDirectory parentDirectory;

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    public String getClassName() {
        return className;
    }

    public T setClassName(String className) {
        this.className = className;
        return self();
    }

    public String getPackageName() {
        return packageName;
    }

    public T setPackageName(String packageName) {
        this.packageName = packageName;
        return self();
    }

    public VirtualFile getSourceRoot() {
        return sourceRoot;
    }

    public T setSourceRoot(VirtualFile sourceRoot) {
        this.sourceRoot = sourceRoot;
        return self();
    }

    public PsiDirectory getParentDirectory() {
        return parentDirectory;
    }

    @SuppressWarnings("WeakerAccess")
    public T setParentDirectory(PsiDirectory parentDirectory) {
        this.parentDirectory = parentDirectory;
        return self();
    }

    public Project getProject() {
        return project;
    }

    public T setProject(Project project) {
        this.project = project;
        return self();
    }

    public abstract String getFileName();

    public abstract CodeWriter getCodeWriter();
}
