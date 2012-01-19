package org.github.olloginov.ideataskbrowser.view;

import com.intellij.tasks.TaskRepository;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

public class TaskTreeModel extends DefaultTreeModel {
    public TaskTreeModel() {
        super(new RootTreeNode());
    }

    public RootTreeNode getRoot() {
        return (RootTreeNode) super.getRoot();
    }

    public void addNode(RepositoryTreeNode node) {
        appendNode(getRoot(), node);
    }

    private void appendNode(MutableTreeNode parent, MutableTreeNode child) {
        insertNodeInto(child, parent, parent.getChildCount());
        if (parent == getRoot()) {
            nodeStructureChanged(getRoot());
        }
    }

    public RepositoryTreeNode getRepositoryNode(TaskRepository repository) {
        for (int index = getRoot().getChildCount() - 1; index >= 0; --index) {
            RepositoryTreeNode repositoryNode = getRoot().getChildAt(index);
            if (repositoryNode.getRepository() == repository) {
                return repositoryNode;
            }
        }
        return null;
    }

    private static class RootTreeNode extends DefaultMutableTreeNode {
        @Override
        public RepositoryTreeNode getChildAt(int index) {
            return (RepositoryTreeNode) super.getChildAt(index);
        }
    }
}
