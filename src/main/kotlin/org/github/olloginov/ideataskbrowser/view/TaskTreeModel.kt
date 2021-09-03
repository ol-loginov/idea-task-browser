package org.github.olloginov.ideataskbrowser.view

import org.github.olloginov.ideataskbrowser.model.TaskSearchList
import org.github.olloginov.ideataskbrowser.util.invokeAndWait
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.MutableTreeNode

class TaskTreeModel(
    list: TaskSearchList = TaskSearchList()
) : DefaultTreeModel(RootTreeNode()) {
    init {
        for (index in 0 until list.size) {
            appendSearch(list, index)
        }

        list.addListDataListener(object : ListDataListener {
            override fun intervalAdded(e: ListDataEvent) {
                invokeAndWait {
                    for (index in e.index0..e.index1) {
                        appendSearch(list, index)
                    }
                }
            }

            override fun intervalRemoved(e: ListDataEvent) {
                invokeAndWait {
                    for (index in e.index1 downTo e.index0) {
                        removeNodeFromParent(getRoot().getChildAt(index))
                    }
                }
            }

            override fun contentsChanged(e: ListDataEvent) = Unit
        })
    }

    override fun getRoot(): RootTreeNode {
        return super.getRoot() as RootTreeNode
    }

    override fun insertNodeInto(newChild: MutableTreeNode?, parent: MutableTreeNode?, index: Int) {
        super.insertNodeInto(newChild, parent, index)
        if (parent == getRoot()) {
            nodeStructureChanged(getRoot())
        }
    }

    private fun appendSearch(list: TaskSearchList, index: Int) {
        appendNode(getRoot(), TaskSearchTreeNode(list.getElementAt(index)))
    }

    private fun appendNode(parent: MutableTreeNode, child: MutableTreeNode) {
        insertNodeInto(child, parent, parent.childCount)
    }

    class RootTreeNode : DefaultMutableTreeNode() {
        override fun getChildAt(index: Int): TaskSearchTreeNode {
            return super.getChildAt(index) as TaskSearchTreeNode
        }
    }
}
