package com.mistraltech.smogen.utils

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

object ActionEventUtils {
    /**
     * Gets the file associated with an action event.
     *
     * @param event the action event
     * @return the associated file
     * @throws IllegalStateException if no associated file is found
     */
    @JvmStatic
    fun getTargetFile(event: AnActionEvent): PsiFile {
        return event.getData(CommonDataKeys.PSI_FILE) ?: throw IllegalStateException("No actionable file")
    }

    /**
     * Gets the project associated with an action event.
     *
     * @param event the action event
     * @return the associated project
     * @throws IllegalStateException if no associated project is found
     */
    @JvmStatic
    fun getTargetProject(event: AnActionEvent): Project {
        return event.getData(CommonDataKeys.PROJECT) ?: throw IllegalStateException("No actionable project")
    }

    /**
     * Gets the editor associated with an action event, if one exists.
     *
     * @param event the action event
     * @return optionally, the associated editor
     */
    @JvmStatic
    fun getOptionalTargetEditor(event: AnActionEvent): Editor? {
        return event.getData(CommonDataKeys.EDITOR)
    }

    /**
     * Determines whether the event has an associated project.
     *
     * @param event the event
     * @return true if the event has an associated project; false otherwise
     */
    @JvmStatic
    fun hasProject(event: AnActionEvent): Boolean {
        return event.getData(CommonDataKeys.PROJECT) != null
    }

    /**
     * Determines whether the event has an associated file.
     *
     * @param event the event
     * @return true if the event has an associated file; false otherwise
     */
    @JvmStatic
    fun hasFile(event: AnActionEvent): Boolean {
        return event.getData(CommonDataKeys.PSI_FILE) != null
    }

    /**
     * Determines whether the event has an associated class.
     *
     * @param event the event
     * @return true if the event has an associated class; false otherwise
     */
    @JvmStatic
    fun hasSelectedClass(event: AnActionEvent): Boolean {
        return hasProject(event) && hasFile(event) && PsiUtils.getSelectedClass(event) != null
    }
}
