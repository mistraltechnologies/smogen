package com.github.richardjwilson.smogen.services

import com.intellij.openapi.project.Project
import com.github.richardjwilson.smogen.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
