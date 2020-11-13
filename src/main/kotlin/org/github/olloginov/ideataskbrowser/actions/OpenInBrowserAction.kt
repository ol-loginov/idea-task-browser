package org.github.olloginov.ideataskbrowser.actions

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import org.github.olloginov.ideataskbrowser.TaskBrowserToolWindow

private const val ID: String = "OpenInBrowser"

class OpenInBrowserAction(
	private val toolWindow: TaskBrowserToolWindow,
	noop: Boolean
) : AnActionImpl(ID, noop) {
	override fun isEnabled(project: Project): Boolean {
		return getIssueUrl() != null
	}

	override fun actionPerformedNow(e: AnActionEvent) {
		val taskUrl = getIssueUrl()
		if (taskUrl != null) {
			BrowserUtil.browse(taskUrl)
		}
	}

	private fun getIssueUrl(): String? {
		val task = toolWindow.getSelectedTask() ?: return null

		val taskUrl = task.issueUrl
		if (taskUrl != null && taskUrl.isNotEmpty()) {
			return taskUrl
		}

		return null
	}
}
