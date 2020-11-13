package org.github.olloginov.ideataskbrowser.view

import javax.swing.tree.MutableTreeNode

class TreeNodeRef<T : MutableTreeNode>(
	private val model: TaskTreeModel,
	val node: T
) {
	fun insertChild(index: Int, child: TaskTreeNode) {
		model.insertNodeInto(child, node, index)
	}

	fun updateChild(child: TaskTreeNode) {
		model.nodeChanged(child)
	}

	fun removeChild(index: Int) {
		node.remove(index)
	}
}
