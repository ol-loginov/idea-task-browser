package org.github.olloginov.ideataskbrowser;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.tasks.Task;
import org.github.olloginov.ideataskbrowser.config.TaskBrowserConfig;
import org.github.olloginov.ideataskbrowser.model.TaskSearchList;
import org.github.olloginov.ideataskbrowser.tasks.FetchNewIssuesTask;
import org.github.olloginov.ideataskbrowser.tasks.ImportNewSearchesTask;
import org.github.olloginov.ideataskbrowser.view.TaskBrowserPanel;
import org.github.olloginov.ideataskbrowser.view.TaskTreeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.TreeNode;

@State(name = "TaskBrowser", storages = {@Storage(file = "$WORKSPACE_FILE$")})
public class TaskBrowserService extends TaskBrowser implements ProjectComponent, PersistentStateComponent<TaskBrowserConfig> {
    private final Project project;

    final TaskSearchList searchList = new TaskSearchList();

    private TaskBrowserPanel panel;
    private SimpleToolWindowPanel panelContainer;

    public TaskBrowserService(Project project) {
        this.project = project;
    }

    @NotNull
    @Override
    public String getComponentName() {
        return COMPONENT_NAME;
    }

    @Override
    public void initComponent() {
        panel = new TaskBrowserPanel();
        panel.setList(searchList);

        panelContainer = panel.wrapInToolWindowPanel();
    }

    @Override
    public void disposeComponent() {
        if (panelContainer != null) {
            panelContainer = null;
        }
        if (panel != null) {
            panel = null;
        }
    }

    @Override
    public void projectOpened() {
        importChanges();
    }

    @Override
    public void projectClosed() {
    }

    @Override
    public TaskBrowserConfig getState() {
        return new TaskBrowserServicePersister(this).save();
    }

    @Override
    public void loadState(TaskBrowserConfig state) {
        new TaskBrowserServicePersister(this).load(state);
    }

    @NotNull
    @Override
    public SimpleToolWindowPanel getPanel() {
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
    public void importChanges() {
        ProgressManager.getInstance().run(new ImportNewSearchesTask(getProject(), searchList));
        ProgressManager.getInstance().run(new FetchNewIssuesTask(getProject(), panel.getTreeModel()));
    }

    @NotNull
    @Override
    public Project getProject() {
        return project;
    }

    @NotNull
    @Override
    public TaskSearchList getSearchList() {
        return searchList;
    }
}
