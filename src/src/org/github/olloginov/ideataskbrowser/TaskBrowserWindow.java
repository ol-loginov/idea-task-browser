package org.github.olloginov.ideataskbrowser;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.tasks.Task;
import com.intellij.tasks.TaskManager;
import com.intellij.tasks.TaskRepository;
import com.intellij.util.continuation.Continuation;
import org.github.olloginov.ideataskbrowser.actions.OpenInBrowserAction;
import org.github.olloginov.ideataskbrowser.actions.OpenInContextAction;
import org.github.olloginov.ideataskbrowser.actions.RefreshListAction;
import org.github.olloginov.ideataskbrowser.model.RepositoryTreeNode;
import org.github.olloginov.ideataskbrowser.model.TaskTreeModel;
import org.github.olloginov.ideataskbrowser.model.TaskTreeNode;
import org.github.olloginov.ideataskbrowser.model.TreeNodeRef;
import org.github.olloginov.ideataskbrowser.tasks.RefreshRepositoryNodeTask;

import javax.swing.tree.TreeNode;
import java.awt.*;

public class TaskBrowserWindow extends SimpleToolWindowPanel implements Disposable, TaskBrowser {
    private static final String TOOL_WINDOW_ID = "TaskBrowser";

    private final TaskBrowserPanel panel = new TaskBrowserPanel();

    public TaskBrowserWindow() {
        super(false);
        add(panel.$$$getRootComponent$$$(), BorderLayout.CENTER);

        final DefaultActionGroup toolbarGroup = new DefaultActionGroup();
        toolbarGroup.add(new RefreshListAction());
        toolbarGroup.add(new OpenInContextAction());
        toolbarGroup.add(new OpenInBrowserAction());

        final ActionManager actionManager = ActionManager.getInstance();
        final ActionToolbar toolbar = actionManager.createActionToolbar(TOOL_WINDOW_ID, toolbarGroup, false);

        panel.setToolbar(toolbar.getComponent());
    }

    @Override
    public Task getSelectedTask() {
        TreeNode node = panel.getSelectedNode();
        if (node != null && node instanceof TaskTreeNode) {
            return ((TaskTreeNode) node).getTask();
        }
        return null;
    }

    @Override
    public void updateList(Project project) {
        Continuation continuation = Continuation.createFragmented(project, true);

        TaskManager taskManager = ServiceManager.getService(project, TaskManager.class);
        TaskTreeModel taskModel = panel.getModel();

        panel.getModel().markRepositoryObsoleteAll();
        for (TaskRepository repository : taskManager.getAllRepositories()) {
            RepositoryTreeNode node = taskModel.getRepositoryNode(repository);
            if (node == null) {
                node = new RepositoryTreeNode(repository);
                taskModel.addNode(node);
            }
            continuation.run(new RefreshRepositoryNodeTask(project, repository, new TreeNodeRef<RepositoryTreeNode>(taskModel, node)));
        }
    }

    @Override
    public void dispose() {
    }
}
