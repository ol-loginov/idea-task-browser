package org.github.olloginov.ideataskbrowser.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import org.github.olloginov.ideataskbrowser.TaskBrowserBundle
import javax.swing.Icon

private fun resolveIcon(key: String): Icon {
	return IconLoader.getIcon("/icons/action.$key.png")
}

private fun resolveDescription(key: String): String {
	return TaskBrowserBundle.message("action.$key.description")
}

private fun resolveText(key: String): String {
	return TaskBrowserBundle.message("action.$key.text")
}

abstract class AnActionImpl(
	key: String,
	private val noop: Boolean
) : AnAction(resolveText(key), resolveDescription(key), resolveIcon(key)) {

	override fun update(e: AnActionEvent) {
		val project = e.project
		e.presentation.isEnabled = project != null && isEnabled(project)
	}

	protected open fun isEnabled(project: Project): Boolean {
		return false
	}

	final override fun actionPerformed(e: AnActionEvent) {
		if (noop) {
			return
		}
		this.actionPerformedNow(e)
	}

	abstract fun actionPerformedNow(e: AnActionEvent)
}
