package com.mistraltech.smogen.plugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.mistraltech.smogen.codegenerator.matchergenerator.MatcherGeneratorProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class MatcherGeneratorOptionsDialog extends DialogWrapper implements MatcherGeneratorOptionsPanelDataSource {
    private static final String RECENTS_KEY = "MatcherGeneratorOptionsDialog.RecentsKey";

    private final Project project;
    private final PsiClass matchedClass;
    private final MatcherGeneratorOptionsPanel matcherGeneratorOptionsPanel;
    private final List<VirtualFile> candidateSourceRoots;
    private final MatcherGeneratorProperties generatorProperties;

    public MatcherGeneratorOptionsDialog(@NotNull Project project,
                                         @NotNull PsiClass matchedClass,
                                         @NotNull List<VirtualFile> candidateSourceRoots,
                                         @NotNull MatcherGeneratorProperties generatorProperties) {
        super(project);

        this.generatorProperties = generatorProperties;
        setModal(true);
        setTitle("Generate Matcher");

        this.project = project;
        this.matchedClass = matchedClass;
        this.candidateSourceRoots = candidateSourceRoots;

        this.matcherGeneratorOptionsPanel = new MatcherGeneratorOptionsPanel(this);

        init();
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();

        final VirtualFile sourceRoot = matcherGeneratorOptionsPanel.getSelectedSourceRoot();

        generatorProperties.setClassName(matcherGeneratorOptionsPanel.getSelectedClassName())
                .setFactoryMethodPrefix(matcherGeneratorOptionsPanel.isAn() ? "an" : "a")
                .setExtensible(matcherGeneratorOptionsPanel.isMakeExtensible())
                .setConcreteSubclassName(matcherGeneratorOptionsPanel.getConcreteSubclassName())
                .setSuperClassName(matcherGeneratorOptionsPanel.getSuperClassName())
                .setPackageName(matcherGeneratorOptionsPanel.getSelectedPackageName())
                .setSourceRoot(sourceRoot);
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        ValidationInfo validationInfo = matcherGeneratorOptionsPanel.doValidate();

        if (validationInfo != null) {
            return validationInfo;
        }

        return super.doValidate();
    }

    @Nullable
    protected JComponent createCenterPanel() {
        return matcherGeneratorOptionsPanel.getRoot();
    }

    @NotNull
    @Override
    public Project getProject() {
        return project;
    }

    @NotNull
    @Override
    public String getRecentsKey() {
        return RECENTS_KEY;
    }

    @NotNull
    @Override
    public String getPackageName() {
        return generatorProperties.getPackageName();
    }

    @NotNull
    @Override
    public PsiClass getMatchedClass() {
        return matchedClass;
    }

    @NotNull
    @Override
    public String getDefaultClassName() {
        return generatorProperties.getClassName();
    }

    @NotNull
    @Override
    public VirtualFile getDefaultRoot() {
        return generatorProperties.getSourceRoot();
    }

    @NotNull
    @Override
    public List<VirtualFile> getCandidateRoots() {
        return candidateSourceRoots;
    }

    @Override
    public boolean getDefaultIsExtensible() {
        return generatorProperties.isExtensible();
    }
}
