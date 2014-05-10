package com.mistraltech.smogen.generator;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.mistraltech.smogen.plugin.ReformatAction;
import com.mistraltech.smogen.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;

public class Generator {

    private final PsiClass sourceClass;
    private final GeneratorProperties generatorProperties;
    private final Project project;

    public Generator(@NotNull final PsiClass sourceClass, @NotNull final GeneratorProperties generatorProperties) {
        this.sourceClass = sourceClass;
        this.project = sourceClass.getProject();
        this.generatorProperties = generatorProperties;
    }

    public void generate() {
        final PsiDirectory parentDirectory = findOrCreateParentDirectory();

        final String fileName = generatorProperties.getClassName() + ".java";
        final PsiFile existingFile = parentDirectory.findFile(fileName);
        if (existingFile != null && !shouldOverwriteFile(existingFile)) {
            // Abort
            return;
        }

        GenerateFileContentRunnable generateFileContentRunnable = new GenerateFileContentRunnable(
                generatorProperties, sourceClass, existingFile, parentDirectory);
        runAction(generateFileContentRunnable);

        final PsiFile targetFile = generateFileContentRunnable.getTargetFile();
        runActionAsCommand(new ReformatAction(targetFile));
    }

    private void runAction(Runnable action) {
        Application application = ApplicationManager.getApplication();
        application.runWriteAction(action);
    }

    private void runActionAsCommand(final Runnable action) {
        CommandProcessor.getInstance().executeCommand(project, new Runnable() {
            @Override
            public void run() {
                ApplicationManager.getApplication().runWriteAction(action);
            }
        }, "", "");
    }

    private void runActionLater(final Runnable generateMatcherClassAction) {
        final Application application = ApplicationManager.getApplication();
        application.invokeLater(new Runnable() {
            @Override
            public void run() {
                application.runWriteAction(generateMatcherClassAction);
            }
        });
    }

    private boolean shouldOverwriteFile(PsiFile existingFile) {
        String msg = "File " + existingFile.getVirtualFile().getPresentableUrl() + " already exists. Overwrite?";
        return Messages.showYesNoDialog(project, msg, "File Already Exists",
                "Overwrite", "Cancel", Messages.getWarningIcon()) == Messages.YES;
    }

    @NotNull
    private PsiDirectory findOrCreateParentDirectory() {
        final PsiDirectory baseDir = PsiManager.getInstance(project).findDirectory(generatorProperties.getSourceRoot());
        assert (baseDir != null);

        // Ensure that the directory for the target package exists
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run() {
                PsiUtils.createMissingDirectoriesForPackage(baseDir, generatorProperties.getPackageName());
            }
        });

        PsiDirectory directory = PsiUtils.findDirectoryForPackage(baseDir, generatorProperties.getPackageName());
        assert (directory != null);

        return directory;
    }
}
