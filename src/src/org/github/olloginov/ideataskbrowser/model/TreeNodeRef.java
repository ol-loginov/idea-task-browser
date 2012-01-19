package org.github.olloginov.ideataskbrowser.model;

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
}
