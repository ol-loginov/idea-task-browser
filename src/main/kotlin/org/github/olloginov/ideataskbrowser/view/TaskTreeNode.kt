package org.github.olloginov.ideataskbrowser.view

import com.intellij.tasks.Task
import com.intellij.ui.ColoredTextContainer
import com.intellij.ui.SimpleTextAttributes
import javax.swing.Icon
import javax.swing.tree.DefaultMutableTreeNode

class TaskTreeNode(task: Task) : DefaultMutableTreeNode(task), CustomIcon, CustomLabel {
	fun getTask(): Task = getUserObject() as Task

	override fun getIcon(): Icon = getTask().icon

	override fun toString(): String = getTask().presentableName

	fun setTask(task: Task) {
		setUserObject(task)
	}

	override fun setLabel(coloredTextContainer: ColoredTextContainer) {
		coloredTextContainer.append(getTask().toString(), SimpleTextAttributes.REGULAR_ATTRIBUTES)
	}
}
