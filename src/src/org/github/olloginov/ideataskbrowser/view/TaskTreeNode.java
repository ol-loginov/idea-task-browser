package org.github.olloginov.ideataskbrowser.view;

import com.intellij.tasks.Task;
import com.intellij.ui.ColoredTextContainer;
import com.intellij.ui.SimpleTextAttributes;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class TaskTreeNode extends DefaultMutableTreeNode implements CustomIcon, CustomLabel {
    public TaskTreeNode(Task task) {
        super(task);
    }

    public Task getTask() {
        return (Task) getUserObject();
    }

    @Override
    public Icon getIcon() {
        return getTask().getIcon();
    }

    @Override
    public String toString() {
        return getTask().getPresentableName();
    }

    public void setTask(Task task) {
        setUserObject(task);
    }

    @Override
    public void setLabel(ColoredTextContainer coloredTextContainer) {
        coloredTextContainer.append(getTask().toString(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
    }
}
