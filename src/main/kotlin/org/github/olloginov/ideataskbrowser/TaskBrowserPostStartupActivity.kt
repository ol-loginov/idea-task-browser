package org.github.olloginov.ideataskbrowser

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

class TaskBrowserPostStartupActivity : StartupActivity {
    override fun runActivity(project: Project) {
        project.getService(TaskBrowser::class.java)?.refresh()
    }
}
