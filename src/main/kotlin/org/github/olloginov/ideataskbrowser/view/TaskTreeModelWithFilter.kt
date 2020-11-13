package org.github.olloginov.ideataskbrowser.view

import com.intellij.tasks.TaskState
import com.intellij.util.EventDispatcher
import javax.swing.event.TreeModelEvent
import javax.swing.event.TreeModelListener
import javax.swing.tree.TreeModel
import javax.swing.tree.TreePath

class TaskTreeModelWithFilter(
	private val model: TaskTreeModel,
	enabledFilters: List<TaskState?>
) : TreeModel {

	private val events: EventDispatcher<TreeModelListener> = EventDispatcher.create(TreeModelListener::class.java)
	private var modelFilter: List<TaskState?> = emptyList()

	init {
		this.model.addTreeModelListener(object : TreeModelListener {
			override fun treeNodesChanged(e: TreeModelEvent) {
				if (e.childIndices == null) {
					events.multicaster.treeNodesChanged(TreeModelEvent(this, e.treePath, null, null))
				} else {
					val localEvent = toLocalEvent(e) ?: return
					events.multicaster.treeNodesChanged(localEvent)
				}
			}

			private fun toLocalEvent(e: TreeModelEvent): TreeModelEvent? {
				val parent = e.treePath.lastPathComponent
				val childIndices = IntArray(e.childIndices.size)
				val children = arrayOfNulls<Any>(childIndices.size)

				var newSize = 0
				for (i in e.children.indices) {
					if (isNodeVisible(e.children[i])) {
						childIndices[i] = getIndexOfChild(parent, e.children[i])
						children[i] = e.children[i]
						newSize++
					}
				}
				if (newSize == 0) {
					return null
				}
				return TreeModelEvent(this, e.treePath, childIndices, children)
			}

			override fun treeNodesInserted(e: TreeModelEvent) {
				val localEvent = toLocalEvent(e) ?: return
				events.multicaster.treeNodesInserted(localEvent)
			}

			override fun treeNodesRemoved(e: TreeModelEvent) {
				treeStructureChanged(e)
			}

			override fun treeStructureChanged(e: TreeModelEvent) {
				events.multicaster.treeStructureChanged(TreeModelEvent(this, e.treePath, null, null))
			}
		})

		setStateFilter(enabledFilters)
	}

	override fun getRoot(): Any = model.root

	override fun getChild(parent: Any, index: Int): Any {
		if (parent is TaskSearchTreeNode) {
			var lookupIndex = index
			for (i in 0 until parent.childCount) {
				val taskNode = parent.getChildAt(i)
				if (isNodeVisible(taskNode)) {
					if (lookupIndex == 0) {
						return taskNode
					}
					--lookupIndex
				}
			}
			throw IllegalStateException("node not found")
		}
		return model.getChild(parent, index)
	}

	private fun isNodeVisible(child: Any): Boolean {
		return child !is TaskTreeNode || isNodeVisible(child)
	}

	private fun isNodeVisible(taskNode: TaskTreeNode): Boolean {
		val modelFilter = this.modelFilter
		return modelFilter.isEmpty() || modelFilter.contains(taskNode.getTask().state)
	}

	override fun getChildCount(parent: Any): Int {
		if (parent is TaskSearchTreeNode) {
			var count = 0
			for (i in 0 until parent.childCount) {
				val taskNode = parent.getChildAt(i)
				if (isNodeVisible(taskNode)) {
					++count
				}
			}
			return count
		}
		return model.getChildCount(parent)
	}

	override fun isLeaf(node: Any): Boolean {
		return model.isLeaf(node)
	}

	override fun valueForPathChanged(path: TreePath, newValue: Any) {
		model.valueForPathChanged(path, newValue)
	}

	override fun getIndexOfChild(parent: Any, child: Any): Int {
		if (parent is TaskSearchTreeNode) {
			var index = 0
			for (i in 0 until parent.childCount) {
				val taskNode = parent.getChildAt(i)
				if (isNodeVisible(taskNode)) {
					if (taskNode == child) {
						return index
					}
					++index
				}
			}
			throw IllegalStateException("node not found")
		}
		return model.getIndexOfChild(parent, child)
	}

	override fun addTreeModelListener(l: TreeModelListener) {
		events.addListener(l)
	}

	override fun removeTreeModelListener(l: TreeModelListener) {
		events.removeListener(l)
	}

	fun setStateFilter(enabledFilters: List<TaskState?>) {
		this.modelFilter = enabledFilters
		events.multicaster.treeStructureChanged(TreeModelEvent(this, model.root.path))
	}
}
