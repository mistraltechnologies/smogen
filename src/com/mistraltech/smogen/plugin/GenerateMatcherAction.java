package com.mistraltech.smogen.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.ClassUtil;
import com.mistraltech.smogen.codegenerator.matchergenerator.MatcherGenerator;
import com.mistraltech.smogen.codegenerator.matchergenerator.MatcherGeneratorProperties;
import com.mistraltech.smogen.utils.PsiUtils;
import com.mistraltech.smogen.utils.SourceRootUtils;

import java.util.List;
import java.util.Optional;

public class GenerateMatcherAction extends AnAction {

    @Override
    public void update(AnActionEvent e) {
        boolean enabled = e.getData(CommonDataKeys.PROJECT) != null
                && e.getData(CommonDataKeys.PSI_FILE) != null
                && PsiUtils.getSelectedClass(e).isPresent();

        Presentation presentation = e.getPresentation();
        presentation.setEnabled(enabled);
        presentation.setVisible(true);
    }

    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getData(CommonDataKeys.PROJECT);
        assert project != null;

        final PsiFile selectedFile = e.getData(CommonDataKeys.PSI_FILE);
        if (selectedFile == null) {
            Messages.showErrorDialog(project, "No selected file.", "Absent Data");
            return;
        }

        final Optional<PsiClass> selectedClass = PsiUtils.getSelectedClass(e);
        if (! selectedClass.isPresent()) {
            Messages.showErrorDialog(project, "No selected class.", "Absent Data");
            return;
        }

        // Note that module may be null if the selected class is in a library rather than the source tree
        final Module module = e.getData(LangDataKeys.MODULE);

        final ConfigurationProperties configurationProperties = new ConfigurationProperties(project);

        // Guess what we are going to call the target matcher class and what its package will be
        String preferredClassName = configurationProperties.getMatcherClassNamePrefix() +
                selectedClass.get().getName() +
                configurationProperties.getMatcherClassNameSuffix();
        String preferredPackageName = ClassUtil.extractPackageName(selectedClass.get().getQualifiedName());

        // Get the list of available source roots
        List<VirtualFile> candidateSourceRoots = SourceRootUtils.getSourceAndTestSourceRoots(project);

        if (candidateSourceRoots.isEmpty()) {
            Messages.showErrorDialog(project, "No source roots have been configured. A source root is required as the target location for the generated class.", "No Source Root");
            return;
        }

        // Guess what the target source root might be
        VirtualFile preferredSourceRoot = SourceRootUtils.getPreferredSourceRootForTestClass(project, module, selectedFile.getVirtualFile())
                .orElseThrow(IllegalStateException::new);

        final MatcherGeneratorProperties generatorProperties = new MatcherGeneratorProperties()
                .setProject(project)
                .setClassName(preferredClassName)
                .setPackageName(preferredPackageName)
                .setSourceRoot(preferredSourceRoot)
                .setSourceClass(selectedClass.get())
                .setFactoryMethodSuffix(configurationProperties.getFactoryMethodSuffix())
                .setTemplateFactoryMethodSuffix(configurationProperties.getTemplateFactoryMethodSuffix())
                .setSetterPrefix(configurationProperties.getSetterPrefix())
                .setSetterSuffix(configurationProperties.getSetterSuffix())
                .setExtensible(configurationProperties.isMakeExtensible())
                .setBaseClassName(configurationProperties.getBaseClass())
                .setGenerateTemplateFactoryMethod(configurationProperties.isGenerateTemplateFactoryMethod())
                .setMakeMethodParametersFinal(configurationProperties.isMakeMethodParametersFinal())
                .setUseReflectingPropertyMatcher(configurationProperties.isUseReflectingPropertyMatcher());

        MatcherGeneratorOptionsDialog matcherGeneratorOptionsDialog = new MatcherGeneratorOptionsDialog(
                project, selectedClass.get(), candidateSourceRoots, generatorProperties);
        matcherGeneratorOptionsDialog.show();

        if (matcherGeneratorOptionsDialog.isOK()) {
            new MatcherGenerator(generatorProperties).generate();
        }
    }
}
