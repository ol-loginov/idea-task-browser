package org.github.olloginov.ideataskbrowser;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.tasks.Task;
import org.github.olloginov.ideataskbrowser.model.TaskSearchEventListener;
import org.github.olloginov.ideataskbrowser.model.TaskSearchList;
import org.github.olloginov.ideataskbrowser.tasks.UpdateRepositoriesTask;
import org.github.olloginov.ideataskbrowser.view.TaskBrowserPanel;
import org.github.olloginov.ideataskbrowser.view.TaskTreeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.TreeNode;

public class TaskBrowserService extends TaskBrowser {
    private final Project project;

    private final TaskSearchList searchList = new TaskSearchList();

    private TaskBrowserPanel panel = new TaskBrowserPanel();
    private SimpleToolWindowPanel panelContainer;

    public TaskBrowserService(Project project) {
        this.project = project;
    }

    @NotNull
    @Override
    public SimpleToolWindowPanel getPanel() {
        if (panelContainer == null) {
            panelContainer = panel.wrapInToolWindowPanel();
        }
        return panelContainer;
    }

    @Nullable
    @Override
    public Task getSelectedTask() {
        TreeNode node = panel.getSelectedNode();
        if (node != null && node instanceof TaskTreeNode) {
            return ((TaskTreeNode) node).getTask();
        }
        return null;
    }

    @Override
    public void refreshAll() {
        ProgressManager.getInstance().run(new UpdateRepositoriesTask(this));
    }

    @NotNull
    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void addListener(TaskSearchEventListener listener) {
        searchList.getDispatcher().addListener(listener);
    }

    @Override
    public void removeListener(TaskSearchEventListener listener) {
        searchList.getDispatcher().removeListener(listener);
    }
}
