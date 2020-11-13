package org.github.olloginov.ideataskbrowser

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

class TaskBrowserPostStartupActivity : StartupActivity {
	override fun runActivity(project: Project) {
		ServiceManager.getService(project, TaskBrowser::class.java)?.refresh()
	}
}
