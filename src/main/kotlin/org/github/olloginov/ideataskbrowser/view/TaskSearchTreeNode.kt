package org.github.olloginov.ideataskbrowser.view

import com.intellij.tasks.Task
import com.intellij.ui.ColoredTextContainer
import com.intellij.ui.SimpleTextAttributes
import org.github.olloginov.ideataskbrowser.model.TaskSearch
import org.github.olloginov.ideataskbrowser.util.TaskHelper
import java.util.*
import javax.swing.Icon
import javax.swing.tree.DefaultMutableTreeNode

class TaskSearchTreeNode(search: TaskSearch) : DefaultMutableTreeNode(search), CustomIcon, CustomLabel {
	fun getSearch(): TaskSearch {
		return getUserObject() as TaskSearch
	}

	override fun toString(): String {
		return String.format("%s", getSearch().getRepository())
	}

	fun findTaskNode(task: Task): Int {
		val children = 0.rangeTo(childCount)
			.map { getChildAt(it) }
			.map { it.getTask() }
			.map { it.id }
			.toTypedArray()
		return Arrays.binarySearch(children, task.id, { a, b -> a.compareTo(b) })
	}

	override fun getChildAt(index: Int): TaskTreeNode {
		return super.getChildAt(index) as TaskTreeNode
	}

	fun getLatestTaskDate(): Date? {
		var date: Date? = null

		for (index in childCount - 1 downTo 0) {
			val taskNode = getChildAt(index)
			val taskChange = TaskHelper.getChangeDate(taskNode.getTask()) ?: continue

			if (date == null || taskChange.after(date)) {
				date = taskChange
			}
		}
		return date
	}

	override fun getIcon(): Icon? {
		return getSearch().getIcon()
	}

	override fun setLabel(coloredTextContainer: ColoredTextContainer) {
		coloredTextContainer.append(toString(), SimpleTextAttributes.REGULAR_ATTRIBUTES)
	}
}