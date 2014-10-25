package com.mistraltech.smogen.plugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface MatcherGeneratorOptionsPanelDataSource {
    @NotNull
    Project getProject();

    @NotNull
    String getRecentsKey();

    @NotNull
    String getPackageName();

    @NotNull
    PsiClass getMatchedClass();

    @NotNull
    String getDefaultClassName();

    @NotNull
    VirtualFile getDefaultRoot();

    @NotNull
    List<VirtualFile> getCandidateRoots();

    boolean getDefaultIsExtensible();
}
