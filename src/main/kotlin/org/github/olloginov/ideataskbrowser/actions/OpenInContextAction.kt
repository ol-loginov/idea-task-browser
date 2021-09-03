package org.github.olloginov.ideataskbrowser.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.tasks.actions.OpenTaskDialog
import org.github.olloginov.ideataskbrowser.TaskBrowserToolWindow

private const val ID = "OpenInContext"

class OpenInContextAction(
    private val toolWindow: TaskBrowserToolWindow,
    noop: Boolean
) : AnActionImpl(ID, noop) {

    override fun isEnabled(project: Project): Boolean {
        return toolWindow.getSelectedTask() != null
    }

    override fun actionPerformedNow(e: AnActionEvent) {
        val project = e.project ?: return
        val task = toolWindow.getSelectedTask() ?: return
        OpenTaskDialog(project, task).show()
    }
}
