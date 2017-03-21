package org.github.olloginov.ideataskbrowser.tasks;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.tasks.TaskManager;
import com.intellij.tasks.TaskRepository;
import org.github.olloginov.ideataskbrowser.TaskBrowserBundle;
import org.github.olloginov.ideataskbrowser.model.TaskSearch;
import org.github.olloginov.ideataskbrowser.model.TaskSearchList;
import org.github.olloginov.ideataskbrowser.view.TaskTreeModel;
import org.jetbrains.annotations.NotNull;

public class UpdateRepositoriesTask extends Task.Backgroundable {
    private final TaskSearchList list;
    private final TaskTreeModel tree;

    public UpdateRepositoriesTask(@NotNull Project project, @NotNull TaskSearchList list, @NotNull TaskTreeModel treeModel) {
        super(project, TaskBrowserBundle.message("UpdateRepositoriesTask.title"), true);
        this.list = list;
        this.tree = treeModel;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        TaskManager taskManager = TaskManager.getManager(myProject);
        for (TaskRepository r : taskManager.getAllRepositories()) {
            TaskSearch search = list.findSearchByRepository(r.getPresentableName());
            if (search == null) {
                indicator.setText(TaskBrowserBundle.message("UpdateRepositoriesTask.updateRepository", r.getPresentableName()));
                search = new TaskSearch();
                search.setQuery("");
                search.setRepository(r.getPresentableName());
                list.add(search);
            }
        }

        indicator.setText(TaskBrowserBundle.message("UpdateRepositoriesTask.title"));
        list.updateIcons(taskManager);

        new FetchNewIssuesTask(getProject(), tree).run(indicator);
    }
}
