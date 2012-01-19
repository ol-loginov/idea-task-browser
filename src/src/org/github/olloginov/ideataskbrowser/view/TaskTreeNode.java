package org.github.olloginov.ideataskbrowser.view;

import com.intellij.tasks.Task;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class TaskTreeNode extends DefaultMutableTreeNode implements CustomIcon {
    private final Task task;

    public TaskTreeNode(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

    @Override
    public Icon getIcon() {
        return TaskTreeRenderer.getIconByType(task.getType());
    }

    @Override
    public String toString() {
        return task.getPresentableName();
    }
}
