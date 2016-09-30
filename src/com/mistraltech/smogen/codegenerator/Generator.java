package com.mistraltech.smogen.codegenerator;

import com.intellij.codeInsight.actions.AbstractLayoutCodeProcessor;
import com.intellij.codeInsight.actions.OptimizeImportsProcessor;
import com.intellij.codeInsight.actions.RearrangeCodeProcessor;
import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.mistraltech.smogen.utils.ActionUtils.runAction;
import static com.mistraltech.smogen.utils.PsiUtils.createMissingDirectoriesForPackage;
import static com.mistraltech.smogen.utils.PsiUtils.findDirectoryForPackage;

public class Generator {

    private final GeneratorProperties generatorProperties;

    public Generator(@NotNull final GeneratorProperties generatorProperties) {
        this.generatorProperties = generatorProperties;
    }

    public void generate() {
        final PsiDirectory parentDirectory = findOrCreateParentDirectory();
        generatorProperties.setParentDirectory(parentDirectory);

        final PsiFile existingFile = parentDirectory.findFile(generatorProperties.getFileName());
        if (existingFile != null && !shouldOverwriteFile(existingFile)) {
            return;
        }

        List<String> warnings = new ArrayList<String>();
        warnings.addAll(checkProperties());
        if (! warnings.isEmpty() && !shouldContinueWithWarnings(warnings)) {
            return;
        }

        final PsiFile generatedFile = generateFile();

        reformat(generatedFile);
    }

    protected List<String> checkProperties() {
        return Collections.emptyList();
    }

    private PsiFile generateFile() {
        FileContentGeneratorRunnable fileContentGeneratorRunnable = new FileContentGeneratorRunnable(generatorProperties);

        runAction(fileContentGeneratorRunnable);

        return fileContentGeneratorRunnable.getTargetFile();
    }

    private void reformat(PsiFile targetFile) {
        AbstractLayoutCodeProcessor processor = new ReformatCodeProcessor(targetFile.getProject(), targetFile, null, false);
        processor = new OptimizeImportsProcessor(processor);
        processor = new RearrangeCodeProcessor(processor);
        processor.run();
    }

    protected boolean shouldOverwriteFile(PsiFile existingFile) {
        String msg = "File " + existingFile.getVirtualFile().getPresentableUrl() + " already exists. Overwrite?";
        return Messages.showYesNoDialog(generatorProperties.getProject(), msg, "File Already Exists",
                "Overwrite", "Cancel", Messages.getWarningIcon()) == Messages.YES;
    }

    protected boolean shouldContinueWithWarnings(List<String> warnings)
    {
        String msg = "The generated source may not compile for the following reasons:\n" + StringUtils.join(warnings, '\n');
        return Messages.showYesNoDialog(generatorProperties.getProject(), msg, "Generated Code Will Not Compile",
                "Continue", "Cancel", Messages.getWarningIcon()) == Messages.YES;
    }

    @NotNull
    private PsiDirectory findOrCreateParentDirectory() {
        PsiManager psiManager = PsiManager.getInstance(generatorProperties.getProject());

        VirtualFile sourceRoot = generatorProperties.getSourceRoot();

        final PsiDirectory baseDir = Optional.ofNullable(psiManager.findDirectory(sourceRoot))
                .orElseThrow(() -> new IllegalStateException("Expected source root directory to exist"));

        // Ensure that the directory for the target package exists
        ApplicationManager.getApplication().runWriteAction(() -> {
            createMissingDirectoriesForPackage(baseDir, generatorProperties.getPackageName());
        });

        PsiDirectory directory = findDirectoryForPackage(baseDir, generatorProperties.getPackageName())
                .orElseThrow(() -> new IllegalStateException("Expected directory for package to have been created"));

        return directory;
    }

    private static class FileContentGeneratorRunnable implements Runnable {
        private final GeneratorProperties generatorProperties;
        private PsiFile targetFile;

        FileContentGeneratorRunnable(final GeneratorProperties generatorProperties) {
            this.generatorProperties = generatorProperties;
        }

        @Override
        public void run() {
            final PsiDirectory parentDirectory = generatorProperties.getParentDirectory();
            final String targetFileName = generatorProperties.getFileName();
            final PsiFile existingFile = parentDirectory.findFile(targetFileName);
            targetFile = existingFile != null ? existingFile : parentDirectory.createFile(targetFileName);

            PsiDocumentManager documentManager = PsiDocumentManager.getInstance(parentDirectory.getProject());
            Document document = Optional.ofNullable(documentManager.getDocument(targetFile))
                    .orElseThrow(() -> new IllegalStateException("Expected document from file " + targetFile.getName()));

            document.setText(generatorProperties.getCodeWriter().writeCode());

            documentManager.commitDocument(document);
        }

        /**
         * Gets the file containing the newly generated matcher class.
         *
         * @return the matcher class file
         */
        PsiFile getTargetFile() {
            return targetFile;
        }
    }
}
