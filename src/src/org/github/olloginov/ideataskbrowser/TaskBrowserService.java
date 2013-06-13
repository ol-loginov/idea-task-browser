package org.github.olloginov.ideataskbrowser;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.tasks.Task;
import com.intellij.util.ui.UIUtil;
import org.github.olloginov.ideataskbrowser.config.TaskBrowserConfig;
import org.github.olloginov.ideataskbrowser.model.TaskSearchList;
import org.github.olloginov.ideataskbrowser.tasks.FetchNewIssuesTask;
import org.github.olloginov.ideataskbrowser.tasks.ImportNewSearchesTask;
import org.github.olloginov.ideataskbrowser.view.TaskBrowserPanel;
import org.github.olloginov.ideataskbrowser.view.TaskTreeModel;
import org.github.olloginov.ideataskbrowser.view.TaskTreeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.TreeNode;

@State(name = "TaskBrowser", storages = @Storage(file = StoragePathMacros.WORKSPACE_FILE))
public class TaskBrowserService extends TaskBrowser implements ProjectComponent {
    private final Project project;

    final TaskSearchList searchList = new TaskSearchList();
    TaskBrowserConfig.DoubleClickAction doubleClickAction = TaskBrowserConfig.DoubleClickAction.NOTHING;

    private TaskTreeModel taskTreeModel;
    private TaskBrowserPanel taskTreePanel;
    private SimpleToolWindowPanel taskTreeContainer;

    public TaskBrowserService(Project project) {
        this.project = project;
        this.taskTreeModel = new TaskTreeModel(searchList);
    }

    @NotNull
    @Override
    public String getComponentName() {
        return COMPONENT_NAME;
    }

    @Override
    public void initComponent() {
        taskTreePanel = new TaskBrowserPanel(this);
        taskTreePanel.setTreeModel(taskTreeModel);

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
        return taskTreeContainer;
    }

    @Nullable
    @Override
    public Task getSelectedTask() {
        TreeNode node = taskTreePanel.getSelectedNode();
        if (node != null && node instanceof TaskTreeNode) {
            return ((TaskTreeNode) node).getTask();
        }
        return null;
    }

    @Override
    public void importChanges() {
        UIUtil.invokeLaterIfNeeded(new Runnable() {
            @Override
            public void run() {
                ProgressManager.getInstance().run(new ImportNewSearchesTask(getProject(), searchList));
                ProgressManager.getInstance().run(new FetchNewIssuesTask(getProject(), taskTreeModel));
            }
        });
    }

    @Override
    public void reloadChanges() {
        UIUtil.invokeLaterIfNeeded(new Runnable() {
            @Override
            public void run() {
                ProgressManager.getInstance().run(new FetchNewIssuesTask(getProject(), taskTreeModel));
            }
        });
    }

    @NotNull
    @Override
    public Project getProject() {
        return project;
    }
}
