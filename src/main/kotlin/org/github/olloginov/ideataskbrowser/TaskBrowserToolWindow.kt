package org.github.olloginov.ideataskbrowser

import com.intellij.tasks.Task

interface TaskBrowserToolWindow {
	fun getSelectedTask(): Task?
}
