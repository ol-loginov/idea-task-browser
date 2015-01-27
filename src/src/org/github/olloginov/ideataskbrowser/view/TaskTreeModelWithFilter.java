package org.github.olloginov.ideataskbrowser.view;

import com.intellij.tasks.TaskState;
import com.intellij.util.EventDispatcher;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.List;

public class TaskTreeModelWithFilter implements TreeModel {
    private final TaskTreeModel model;
    private final EventDispatcher<TreeModelListener> events = EventDispatcher.create(TreeModelListener.class);
    private List<TaskState> modelFilter;

    public TaskTreeModelWithFilter(TaskTreeModel model, List<TaskState> enabledFilters) {
        this.model = model;
        this.model.addTreeModelListener(new TreeModelListener() {
            @Override
            public void treeNodesChanged(TreeModelEvent e) {
                if (e.getChildIndices() == null) {
                    events.getMulticaster().treeNodesChanged(new TreeModelEvent(this, e.getTreePath(), null, null));
                } else {
                    TreeModelEvent localEvent = toLocalEvent(e);
                    if (localEvent == null) {
                        return;
                    }
                    events.getMulticaster().treeNodesChanged(localEvent);
                }
            }

            private TreeModelEvent toLocalEvent(TreeModelEvent e) {
                Object parent = e.getTreePath().getLastPathComponent();
                int[] childIndices = new int[e.getChildIndices().length];
                Object[] children = new Object[childIndices.length];
                int newSize = 0;
                for (int i = 0; i < e.getChildren().length; ++i) {
                    if (isNodeVisible(e.getChildren()[i])) {
                        childIndices[i] = getIndexOfChild(parent, e.getChildren()[i]);
                        children[i] = e.getChildren()[i];
                        newSize++;
                    }
                }
                if (newSize == 0) {
                    return null;
                }
                return new TreeModelEvent(this, e.getTreePath(), childIndices, children);
            }

            @Override
            public void treeNodesInserted(TreeModelEvent e) {
                TreeModelEvent localEvent = toLocalEvent(e);
                if (localEvent == null) {
                    return;
                }
                events.getMulticaster().treeNodesInserted(localEvent);
            }

            @Override
            public void treeNodesRemoved(TreeModelEvent e) {
                throw new IllegalStateException("not implemented");
            }

            @Override
            public void treeStructureChanged(TreeModelEvent e) {
                events.getMulticaster().treeStructureChanged(new TreeModelEvent(this, e.getTreePath(), null, null));
            }
        });
        setStateFilter(enabledFilters);
    }

    @Override
    public Object getRoot() {
        return model.getRoot();
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (parent instanceof TaskSearchTreeNode) {
            TaskSearchTreeNode searchTreeNode = (TaskSearchTreeNode) parent;
            for (int i = 0; i < searchTreeNode.getChildCount(); ++i) {
                TaskTreeNode taskNode = searchTreeNode.getChildAt(i);
                if (isNodeVisible(taskNode)) {
                    if (index == 0) {
                        return taskNode;
                    }
                    --index;
                }
            }
            throw new IllegalStateException("node not found");
        }
        return model.getChild(parent, index);
    }

    private boolean isNodeVisible(Object child) {
        return !(child instanceof TaskTreeNode) || isNodeVisible((TaskTreeNode) child);
    }

    private boolean isNodeVisible(TaskTreeNode taskNode) {
        return modelFilter == null || modelFilter.contains(taskNode.getTask().getState());
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent instanceof TaskSearchTreeNode) {
            TaskSearchTreeNode searchTreeNode = (TaskSearchTreeNode) parent;
            int count = 0;
            for (int i = 0; i < searchTreeNode.getChildCount(); ++i) {
                TaskTreeNode taskNode = searchTreeNode.getChildAt(i);
                if (isNodeVisible(taskNode)) {
                    ++count;
                }
            }
            return count;
        }
        return model.getChildCount(parent);
    }

    @Override
    public boolean isLeaf(Object node) {
        return model.isLeaf(node);
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        model.valueForPathChanged(path, newValue);
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if (parent instanceof TaskSearchTreeNode) {
            TaskSearchTreeNode searchTreeNode = (TaskSearchTreeNode) parent;
            int index = 0;
            for (int i = 0; i < searchTreeNode.getChildCount(); ++i) {
                TaskTreeNode taskNode = searchTreeNode.getChildAt(i);
                if (isNodeVisible(taskNode)) {
                    if (taskNode == child) {
                        return index;
                    }
                    ++index;
                }
            }
            throw new IllegalStateException("node not found");
        }
        return model.getIndexOfChild(parent, child);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        events.addListener(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        events.removeListener(l);
    }

    public void setStateFilter(List<TaskState> enabledFilters) {
        this.modelFilter = enabledFilters.isEmpty() ? null : enabledFilters;
        events.getMulticaster().treeStructureChanged(new TreeModelEvent(this, model.getRoot().getPath()));
    }
}
