package org.github.olloginov.ideataskbrowser.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import org.github.olloginov.ideataskbrowser.TaskBrowser

class RefreshListAction(
    noop: Boolean
) : AnActionImpl("RefreshList", noop) {
    override fun isEnabled(project: Project): Boolean {
        return true
    }

    override fun actionPerformedNow(e: AnActionEvent) {
        e.project?.getService(TaskBrowser::class.java)?.refresh()
    }
}
