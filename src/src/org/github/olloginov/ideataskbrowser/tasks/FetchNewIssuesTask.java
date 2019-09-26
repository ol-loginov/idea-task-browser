package org.github.olloginov.ideataskbrowser.tasks;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.github.olloginov.ideataskbrowser.TaskBrowserBundle;
import org.github.olloginov.ideataskbrowser.view.TaskSearchTreeNode;
import org.github.olloginov.ideataskbrowser.view.TaskTreeModel;
import org.github.olloginov.ideataskbrowser.view.TreeNodeRef;
import org.jetbrains.annotations.NotNull;

public class FetchNewIssuesTask extends Task.Backgroundable {
    private final TaskTreeModel treeModel;

    public FetchNewIssuesTask(@NotNull Project project, @NotNull TaskTreeModel treeModel) {
        super(project, TaskBrowserBundle.message("FetchNewIssuesTask.title"), true);
        this.treeModel = treeModel;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        TaskTreeModel.RootTreeNode rootTreeNode = treeModel.getRoot();
        for (int index = 0; index < rootTreeNode.getChildCount(); ++index) {
            final TreeNodeRef<TaskSearchTreeNode> target = new TreeNodeRef<>(treeModel, rootTreeNode.getChildAt(index));
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    ProgressManager.getInstance().run(new FetchNewIssuesFromRepoTask(myProject, target));
                }
            });
        }
    }
}
