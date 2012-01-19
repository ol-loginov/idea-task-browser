package org.github.olloginov.ideataskbrowser.view;

import com.intellij.tasks.Task;
import com.intellij.tasks.TaskRepository;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Date;

public class RepositoryTreeNode extends DefaultMutableTreeNode implements CustomIcon {
    public RepositoryTreeNode(TaskRepository repository) {
        super(repository);
    }

    public TaskRepository getRepository() {
        return (TaskRepository) getUserObject();
    }

    @Override
    public String toString() {
        return String.format("%s", getRepository().getPresentableName());
    }

    public int findTaskNode(Task task) {
        for (int index = 0, end = getChildCount(); index < end; ++index) {
            TaskTreeNode taskNode = getChildAt(index);
            if (task.getId().equals(taskNode.getTask().getId())) {
                return index;
            }

            Date taskCreated = task.getCreated();
            if (taskCreated == null) {
                throw new IllegalStateException("Task should have 'created at' attribute");
            }

            if (taskCreated.after(taskNode.getTask().getCreated())) {
                return -(index + 1);
            }
        }
        return -1;
    }

    @Override
    public TaskTreeNode getChildAt(int index) {
        return (TaskTreeNode) super.getChildAt(index);
    }

    public Date getLatestTaskDate() {
        if (getChildCount() <= 0) {
            return null;
        }
        return getChildAt(0).getTask().getCreated();
    }

    @Override
    public Icon getIcon() {
        return getRepository().getRepositoryType().getIcon();
    }
}