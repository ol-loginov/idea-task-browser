package org.github.olloginov.ideataskbrowser.view;

import com.intellij.tasks.Task;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class TaskTreeNode extends DefaultMutableTreeNode implements CustomIcon {
    public TaskTreeNode(Task task) {
        super(task);
    }

    public Task getTask() {
        return (Task) getUserObject();
    }

    @Override
    public Icon getIcon() {
        return TaskTreeRenderer.getIconByType(getTask().getType());
    }

    @Override
    public String toString() {
        return getTask().getPresentableName();
    }

    public void setTask(Task task) {
        setUserObject(task);
    }
}
