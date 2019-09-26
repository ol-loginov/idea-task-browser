package org.github.olloginov.ideataskbrowser;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.tasks.Task;
import com.intellij.tasks.TaskState;
import org.github.olloginov.ideataskbrowser.config.TaskBrowserConfig;
import org.github.olloginov.ideataskbrowser.model.TaskSearchList;
import org.github.olloginov.ideataskbrowser.tasks.FetchNewIssuesTask;
import org.github.olloginov.ideataskbrowser.tasks.UpdateRepositoriesTask;
import org.github.olloginov.ideataskbrowser.view.TaskBrowserPanel;
import org.github.olloginov.ideataskbrowser.view.TaskTreeModel;
import org.github.olloginov.ideataskbrowser.view.TaskTreeModelWithFilter;
import org.github.olloginov.ideataskbrowser.view.TaskTreeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@State(name = "TaskBrowser", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
public class TaskBrowserService extends TaskBrowser implements ProjectComponent, TaskBrowserServiceState {
    private final Project project;

    final TaskSearchList searchList = new TaskSearchList();

    private final List<TaskState> searchFilters = new ArrayList<>();
    TaskBrowserConfig.DoubleClickAction doubleClickAction = TaskBrowserConfig.DoubleClickAction.NOTHING;

    private TaskTreeModel taskTreeModel;
    private TaskTreeModelWithFilter taskTreeModelWithFilter;

    private TaskBrowserPanel taskTreePanel;
    private SimpleToolWindowPanel taskTreeContainer;

    public TaskBrowserService(Project project) {
        this.project = project;
        this.taskTreeModel = new TaskTreeModel(searchList);
        this.taskTreeModelWithFilter = new TaskTreeModelWithFilter(taskTreeModel, getEnabledFilters());
    }

    @NotNull
    @Override
    public String getComponentName() {
        return COMPONENT_NAME;
    }

    @Override
    public void initComponent() {
        taskTreePanel = new TaskBrowserPanel(this);
        taskTreePanel.setTreeModel(taskTreeModelWithFilter);

        taskTreeContainer = taskTreePanel.wrapInToolWindowPanel();
    }

    @Override
    public void disposeComponent() {
        if (taskTreeContainer != null) {
            taskTreeContainer = null;
        }
        if (taskTreePanel != null) {
            taskTreePanel = null;
        }
    }

    @Override
    public void projectOpened() {
        refresh();
    }

    @Override
    public void projectClosed() {
    }

    @Override
    public TaskBrowserConfig getState() {
        return new TaskBrowserServicePersister(this).save();
    }

    @Override
    public void loadState(@NotNull TaskBrowserConfig state) {
        new TaskBrowserServicePersister(this).load(state);
    }

    @NotNull
    @Override
    public SimpleToolWindowPanel getPanel() {
        return taskTreeContainer;
    }

    @Nullable
    @Override
    public Task getSelectedTask() {
        TreeNode node = taskTreePanel.getSelectedNode();
        if (node instanceof TaskTreeNode) {
            return ((TaskTreeNode) node).getTask();
        }
        return null;
    }

    @Override
    public void refresh() {
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
                ProgressManager.getInstance().run(new UpdateRepositoriesTask(getProject(), searchList, taskTreeModel));
            }
        });
    }

    @Override
    public void reloadChanges() {
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
                taskTreeModelWithFilter.setStateFilter(getEnabledFilters());
                ProgressManager.getInstance().run(new FetchNewIssuesTask(getProject(), taskTreeModel));
            }
        });
    }

    @NotNull
    @Override
    public Project getProject() {
        return project;
    }

    public List<TaskState> getEnabledFilters() {
        return Collections.unmodifiableList(searchFilters);
    }

    @Override
    public boolean isFilterEnabled(TaskState target) {
        return searchFilters.contains(target);
    }

    @Override
    public void setFilterEnabled(TaskState target, boolean state) {
        boolean enabled = isFilterEnabled(target);
        if (enabled == state) {
            return;
        }

        if (state) {
            searchFilters.add(target);
        } else {
            searchFilters.remove(target);
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                taskTreeModelWithFilter.setStateFilter(getEnabledFilters());
            }
        });
    }
}
