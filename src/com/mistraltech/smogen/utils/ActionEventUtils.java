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

    /**
     * Determines whether the event has an associated project.
     *
     * @param event the event
     * @return true if the event has an associated project; false otherwise
     */
    public static boolean hasProject(AnActionEvent event) {
        return event.getData(CommonDataKeys.PROJECT) != null;
    }

    /**
     * Determines whether the event has an associated file.
     *
     * @param event the event
     * @return true if the event has an associated file; false otherwise
     */
    public static boolean hasFile(AnActionEvent event) {
        return event.getData(CommonDataKeys.PSI_FILE) != null;
    }

    /**
     * Determines whether the event has an associated class.
     *
     * @param event the event
     * @return true if the event has an associated class; false otherwise
     */
    public static boolean hasSelectedClass(AnActionEvent event) {
        return hasProject(event) && hasFile(event) && PsiUtils.getSelectedClass(event).isPresent();
    }
}
