package org.github.olloginov.ideataskbrowser.view;

import com.intellij.tasks.Task;
import org.github.olloginov.ideataskbrowser.model.TaskSearch;
import org.github.olloginov.ideataskbrowser.util.TaskHelper;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Arrays;
import java.util.Comparator;
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
        String children[] = new String[getChildCount()];
        for (int i = 0; i < children.length; ++i) {
            children[i] = getChildAt(i).getTask().getId();
        }
        return Arrays.binarySearch(children, task.getId(), new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return a.compareTo(b);
            }
        });
    }

    @Override
    public TaskTreeNode getChildAt(int index) {
        return (TaskTreeNode) super.getChildAt(index);
    }

    public Date getLatestTaskDate() {
        Date date = null;
        for (int index = getChildCount() - 1; index >= 0; --index) {
            TaskTreeNode taskNode = getChildAt(index);

            Date taskChange = TaskHelper.getChangeDate(taskNode.getTask());
            if (taskChange == null) {
                continue;
            }

            if (date == null || taskChange.after(date)) {
                date = taskChange;
            }
        }
        return date;
    }

    @Override
    public Icon getIcon() {
        return getSearch().getIcon();
    }
}