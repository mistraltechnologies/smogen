package com.mistraltech.smogen.plugin;

import com.intellij.ide.util.PackageUtil;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.ClassUtil;
import com.mistraltech.smogen.generator.Generator;
import com.mistraltech.smogen.generator.GeneratorProperties;
import com.mistraltech.smogen.utils.PsiUtils;
import com.mistraltech.smogen.utils.SourceRootUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.java.JavaModuleSourceRootTypes;
import org.jetbrains.jps.model.java.JavaSourceRootType;

import java.util.List;

public class GenerateMatcherAction extends AnAction {

    @Override
    public void update(AnActionEvent e) {
        boolean enabled = e.getData(CommonDataKeys.PROJECT) != null
                && e.getData(CommonDataKeys.PSI_FILE) != null
                && getSelectedClass(e) != null;

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

        final PsiClass selectedClass = getSelectedClass(e);
        if (selectedClass == null) {
            Messages.showErrorDialog(project, "No selected class.", "Absent Data");
            return;
        }

        final Module module = e.getData(LangDataKeys.MODULE);
        if (module == null) {
            Messages.showErrorDialog(project, "No module.", "Absent Data");
            return;
        }

        // Guess what we are going to call the target matcher class and what its package will be
        String preferredClassName = selectedClass.getName() + "Matcher";
        String preferredPackageName = ClassUtil.extractPackageName(selectedClass.getQualifiedName());

        // Make sure the user has configured at least one source root to choose as the target location
        PackageUtil.checkSourceRootsConfigured(module);

        // Guess what the target source root might be
        VirtualFile preferredSourceRoot = getPreferredSourceRoot(project, module, selectedFile.getVirtualFile());

        // Get the list of available source roots
        List<VirtualFile> candidateSourceRoots = SourceRootUtils.getSourceRoots(project, JavaModuleSourceRootTypes.SOURCES, true);

        final GeneratorProperties generatorProperties = new GeneratorProperties()
                .setClassName(preferredClassName)
                .setPackageName(preferredPackageName)
                .setSourceRoot(preferredSourceRoot);

        TargetSelectionDialog targetSelectionDialog = new TargetSelectionDialog(project, selectedClass, candidateSourceRoots, generatorProperties);
        targetSelectionDialog.show();

        if (targetSelectionDialog.isOK())
        {
            new Generator(selectedClass, generatorProperties).generate();
        }
    }

    @Nullable
    private VirtualFile getPreferredSourceRoot(@NotNull Project project, @NotNull Module module, @Nullable VirtualFile selectedFile) {
        List<VirtualFile> validModuleTestSourceRoots = SourceRootUtils.getSourceRoots(module, JavaSourceRootType.TEST_SOURCE, true);

        if (validModuleTestSourceRoots.size() > 0) {
            return validModuleTestSourceRoots.get(0);
        }

        List<VirtualFile> validModuleSourceRoots = SourceRootUtils.getSourceRoots(module, JavaSourceRootType.SOURCE, true);

        if (validModuleSourceRoots.size() > 0) {
            VirtualFile currentSourceRoot = selectedFile != null ?
                    ProjectRootManager.getInstance(project).getFileIndex().getSourceRootForFile(selectedFile) : null;

            if (currentSourceRoot != null && validModuleSourceRoots.contains(currentSourceRoot)) {
                return currentSourceRoot;
            } else {
                return validModuleTestSourceRoots.get(0);
            }
        }

        return null;
    }

    @Nullable
    private PsiClass getSelectedClass(@NotNull AnActionEvent e) {
        final PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        assert file != null;

        final Project project = e.getData(CommonDataKeys.PROJECT);
        assert project != null;

        final DataContext dataContext = e.getDataContext();
        final Editor editor = e.getData(CommonDataKeys.EDITOR);

        final PsiElement psiElement = PsiUtils.getCurrentElement(dataContext, project, editor);

        PsiClass selectedClass = (psiElement != null) ? PsiUtils.findClassFromElement(psiElement) : null;

        if (selectedClass == null) {
            selectedClass = PsiUtils.getClassFromFile(file);
        }

        return selectedClass;
    }
}
