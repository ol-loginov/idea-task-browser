package org.github.olloginov.ideataskbrowser.view

import com.intellij.tasks.Task
import com.intellij.ui.ColoredTextContainer
import com.intellij.ui.SimpleTextAttributes
import org.github.olloginov.ideataskbrowser.model.TaskSearch
import java.util.Arrays
import javax.swing.Icon
import javax.swing.tree.DefaultMutableTreeNode

class TaskSearchTreeNode(search: TaskSearch) : DefaultMutableTreeNode(search), CustomIcon, CustomLabel {
    fun getSearch(): TaskSearch {
        return getUserObject() as TaskSearch
    }

    override fun toString(): String {
        return getSearch().getRepository()
    }

    fun findTaskNode(task: Task): Int {
        val children = 0.until(childCount)
            .map { getChildAt(it) }
            .map { it.getTask() }
            .map { it.id }
            .toTypedArray()
        return Arrays.binarySearch(children, task.id) { a, b -> a.compareTo(b) }
    }

    override fun getChildAt(index: Int): TaskTreeNode {
        return super.getChildAt(index) as TaskTreeNode
    }

    override fun getIcon(): Icon? {
        return getSearch().getIcon()
    }

    override fun setLabel(coloredTextContainer: ColoredTextContainer) {
        coloredTextContainer.append(toString(), SimpleTextAttributes.REGULAR_ATTRIBUTES)
    }
}
