package com.mistraltech.smogen.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ActionEventUtils {
    /**
     * Gets the file associated with an action event.
     *
     * @param event the action event
     * @return the associated file
     * @throws IllegalStateException if no associated file is found
     */
    @NotNull
    public static PsiFile getTargetFile(@NotNull AnActionEvent event) {
        return Optional.ofNullable(event.getData(CommonDataKeys.PSI_FILE))
                .orElseThrow(() -> new IllegalStateException("No actionable file"));
    }

    /**
     * Gets the project associated with an action event.
     *
     * @param event the action event
     * @return the associated project
     * @throws IllegalStateException if no associated project is found
     */
    @NotNull
    public static Project getTargetProject(@NotNull AnActionEvent event) {
        return Optional.ofNullable(event.getData(CommonDataKeys.PROJECT))
                .orElseThrow(() -> new IllegalStateException("No actionable project"));
    }

    /**
     * Gets the editor associated with an action event, if one exists.
     *
     * @param event the action event
     * @return optionally, the associated editor
     */
    @NotNull
    public static Optional<Editor> getOptionalTargetEditor(@NotNull AnActionEvent event) {
        return Optional.ofNullable(event.getData(CommonDataKeys.EDITOR));
    }
}
