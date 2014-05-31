package com.mistraltech.smogen.plugin;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
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

        // Note that module may be null if the selected class is in a library rather than the source tree
        final Module module = e.getData(LangDataKeys.MODULE);

        // Guess what we are going to call the target matcher class and what its package will be
        String preferredClassName = selectedClass.getName() + "Matcher";
        String preferredPackageName = ClassUtil.extractPackageName(selectedClass.getQualifiedName());

        // Get the list of available source roots
        List<VirtualFile> candidateSourceRoots = SourceRootUtils.getSourceRoots(project, JavaModuleSourceRootTypes.SOURCES, true);

        if (candidateSourceRoots.isEmpty()) {
            Messages.showErrorDialog(project, "No source roots have been configured. A source root is required as the target location for the generated class.", "No Source Root");
            return;
        }

        // Guess what the target source root might be
        VirtualFile preferredSourceRoot = getPreferredSourceRoot(project, module, selectedFile.getVirtualFile());

        final GeneratorProperties generatorProperties = new GeneratorProperties()
                .setClassName(preferredClassName)
                .setPackageName(preferredPackageName)
                .setSourceRoot(preferredSourceRoot);

        TargetSelectionDialog targetSelectionDialog = new TargetSelectionDialog(project, selectedClass, candidateSourceRoots, generatorProperties);
        targetSelectionDialog.show();

        if (targetSelectionDialog.isOK()) {
            new Generator(selectedClass, generatorProperties).generate();
        }
    }

    /**
     * Gets the most suitable source root from those available.
     * <p/>
     * Returns the first matching case:
     * <ol>
     * <li>The selected file belongs to a module, that module has test roots and the selected file belongs one of those test roots => that test root</li>
     * <li>The selected file belongs to a module and that module has test roots => the first test root in the module</li>
     * <li>The selected file belongs to a module, that module has source roots and the selected file belongs one of those source roots => that source root</li>
     * <li>The selected file belongs to a module and that module has source roots = the first source root in the module</li>
     * <li>The project has test roots => the first test root</li>
     * <li>The project has source roots => the first source root</li>
     * <li>Otherwise returns Null</li>
     * </ol>
     *
     * @param project the project
     * @param module optional current module - will be null if a library class is selected
     * @param selectedFile the currently selected file
     * @return the preferred source root if one exists; otherwise null
     */
    @Nullable
    private VirtualFile getPreferredSourceRoot(@NotNull Project project, @Nullable Module module, @Nullable VirtualFile selectedFile) {

        VirtualFile preferredSourceRoot = null;

        VirtualFile currentSourceRoot = selectedFile != null ?
                ProjectRootManager.getInstance(project).getFileIndex().getSourceRootForFile(selectedFile) : null;

        List<VirtualFile> candidateSourceRoots = null;

        if (module != null) {
            List<VirtualFile> validModuleTestSourceRoots = SourceRootUtils.getSourceRoots(module, JavaSourceRootType.TEST_SOURCE, true);
            if (validModuleTestSourceRoots.size() > 0) {
                candidateSourceRoots = validModuleTestSourceRoots;
            } else {
                List<VirtualFile> validModuleSourceRoots = SourceRootUtils.getSourceRoots(module, JavaSourceRootType.SOURCE, true);

                if (validModuleSourceRoots.size() > 0) {
                    candidateSourceRoots = validModuleSourceRoots;
                }
            }
        } else {
            List<VirtualFile> validProjectTestSourceRoots = SourceRootUtils.getSourceRoots(project, JavaSourceRootType.TEST_SOURCE, true);
            if (validProjectTestSourceRoots.size() > 0) {
                candidateSourceRoots = validProjectTestSourceRoots;
            } else {
                List<VirtualFile> validProjectSourceRoots = SourceRootUtils.getSourceRoots(project, JavaSourceRootType.SOURCE, true);

                if (validProjectSourceRoots.size() > 0) {
                    candidateSourceRoots = validProjectSourceRoots;
                }
            }
        }

        if (candidateSourceRoots != null) {
            if (currentSourceRoot != null && candidateSourceRoots.contains(currentSourceRoot)) {
                preferredSourceRoot = currentSourceRoot;
            } else {
                preferredSourceRoot = candidateSourceRoots.get(0);
            }
        }

        return preferredSourceRoot;
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
