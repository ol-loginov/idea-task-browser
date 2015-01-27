package org.github.olloginov.ideataskbrowser.view;

import com.intellij.util.ui.UIUtil;
import org.github.olloginov.ideataskbrowser.model.TaskSearchList;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

public class TaskTreeModel extends DefaultTreeModel {
    public TaskTreeModel() {
        this(new TaskSearchList());
    }

    public TaskTreeModel(final TaskSearchList list) {
        super(new RootTreeNode());

        for (int index = 0; index < list.getSize(); ++index) {
            appendSearch(list, index);
        }

        list.addListDataListener(new ListDataListener() {
            @Override
            public void intervalAdded(final ListDataEvent e) {
                UIUtil.invokeAndWaitIfNeeded(new Runnable() {
                    @Override
                    public void run() {
                        for (int index = e.getIndex0(), high = e.getIndex1(); index <= high; ++index) {
                            appendSearch(list, index);
                        }
                    }
                });
            }

            @Override
            public void intervalRemoved(final ListDataEvent e) {
                UIUtil.invokeAndWaitIfNeeded(new Runnable() {
                    @Override
                    public void run() {
                        for (int index = e.getIndex1(), low = e.getIndex0(); index >= low; --index) {
                            removeNodeFromParent(getRoot().getChildAt(index));
                        }
                    }
                });
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
            }
        });
    }

    public RootTreeNode getRoot() {
        return (RootTreeNode) super.getRoot();
    }

    @Override
    public void insertNodeInto(MutableTreeNode newChild, MutableTreeNode parent, int index) {
        super.insertNodeInto(newChild, parent, index);
        if (parent == getRoot()) {
            nodeStructureChanged(getRoot());
        }
    }

    private void appendSearch(TaskSearchList list, int index) {
        appendNode(getRoot(), new TaskSearchTreeNode(list.getElementAt(index)));
    }

    private void appendNode(MutableTreeNode parent, MutableTreeNode child) {
        insertNodeInto(child, parent, parent.getChildCount());
    }

    public static class RootTreeNode extends DefaultMutableTreeNode {
        @Override
        public TaskSearchTreeNode getChildAt(int index) {
            return (TaskSearchTreeNode) super.getChildAt(index);
        }
    }
}
