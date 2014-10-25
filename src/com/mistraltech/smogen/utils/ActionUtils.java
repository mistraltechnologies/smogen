package com.mistraltech.smogen.utils;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;

public class ActionUtils {
    public static void runAction(Runnable action) {
        Application application = ApplicationManager.getApplication();
        application.runWriteAction(action);
    }

    public static void runActionAsCommand(final Runnable action, final Project project) {
        CommandProcessor.getInstance().executeCommand(project, new Runnable() {
            @Override
            public void run() {
                ApplicationManager.getApplication().runWriteAction(action);
            }
        }, "", "");
    }

    public static void runActionLater(final Runnable action) {
        final Application application = ApplicationManager.getApplication();
        application.invokeLater(new Runnable() {
            @Override
            public void run() {
                application.runWriteAction(action);
            }
        });
    }
}
