package com.mistraltech.smogen.services

import com.intellij.openapi.project.Project
import com.mistraltech.smogen.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
