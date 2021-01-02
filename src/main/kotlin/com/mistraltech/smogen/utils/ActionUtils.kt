package com.mistraltech.smogen.utils

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.project.Project

object ActionUtils {
    @JvmStatic
    fun runAction(action: Runnable?) {
        ApplicationManager.getApplication().runWriteAction(action!!)
    }

    fun runActionAsCommand(action: Runnable?, project: Project?) {
        CommandProcessor.getInstance().executeCommand(project, { runAction(action) }, "", "")
    }

    fun runActionLater(action: Runnable?) {
        ApplicationManager.getApplication().invokeLater { runAction(action) }
    }
}
