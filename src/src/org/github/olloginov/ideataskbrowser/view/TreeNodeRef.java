package org.github.olloginov.ideataskbrowser.view;

import javax.swing.tree.MutableTreeNode;

public class TreeNodeRef<T extends MutableTreeNode> {
    private final TaskTreeModel model;
    private final T node;

    public TreeNodeRef(TaskTreeModel model, T node) {
        this.model = model;
        this.node = node;
    }

    public T getNode() {
        return node;
    }

    public void insertChild(int index, TaskTreeNode child) {
        model.insertNodeInto(child, node, index);
    }

    public void updateChild(final TaskTreeNode child) {
        model.nodeChanged(child);
    }

    public void removeChild(int index) {
        node.remove(index);
    }
}
