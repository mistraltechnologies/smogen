package com.mistraltech.smogen.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;

public class ActionUtils {
    public static void runAction(Runnable action) {
        ApplicationManager.getApplication().runWriteAction(action);
    }

    public static void runActionAsCommand(final Runnable action, final Project project) {
        CommandProcessor.getInstance().executeCommand(project, () -> runAction(action), "", "");
    }

    public static void runActionLater(final Runnable action) {
        ApplicationManager.getApplication().invokeLater(() -> runAction(action));
    }

}
