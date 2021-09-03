package org.github.olloginov.ideataskbrowser

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import org.github.olloginov.ideataskbrowser.view.TaskBrowserPanel

class ToolWindowFactory : com.intellij.openapi.wm.ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val cm = toolWindow.contentManager
        val service = ServiceManager.getService(project, TaskBrowser::class.java) ?: throw IllegalStateException("Task browser service not ready")

        val taskTreePanel = TaskBrowserPanel(project)
        taskTreePanel.setTreeModel(service.getFilteredModel())

        cm.addContent(cm.factory.createContent(taskTreePanel.wrapInToolWindowPanel(), null, false))
    }
}
