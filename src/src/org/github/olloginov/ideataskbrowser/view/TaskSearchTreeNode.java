package org.github.olloginov.ideataskbrowser.view;

import com.intellij.tasks.Task;
import org.github.olloginov.ideataskbrowser.model.TaskSearch;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Date;

public class TaskSearchTreeNode extends DefaultMutableTreeNode implements CustomIcon {
    public TaskSearchTreeNode(TaskSearch search) {
        super(search);
    }

    public TaskSearch getSearch() {
        return (TaskSearch) getUserObject();
    }

    @Override
    public String toString() {
        return String.format("%s", getSearch().getRepository());
    }

    public int findTaskNode(Task task) {
        int index = 0;
        for (int end = getChildCount(); index < end; ++index) {
            TaskTreeNode taskNode = getChildAt(index);
            if (task.getId().equals(taskNode.getTask().getId())) {
                return index;
            }

            Date taskCreated = task.getCreated();
            if (taskCreated == null) {
                throw new IllegalStateException("Task should have 'created at' attribute");
            }

            if (taskCreated.after(taskNode.getTask().getCreated())) {
                break;
            }
        }
        return -(index + 1);
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
        return getSearch().getIcon();
    }
}