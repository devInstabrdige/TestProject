package com.example.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.cc.base.logger

class TestGradlePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("greet") {task ->
            task.doLast {
                println("Hello from MyPlugin!")
                logger.info("Hello from MyPlugin!")
            }
        }
    }
}