package org.github.olloginov.ideataskbrowser.tasks;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.tasks.TaskManager;
import com.intellij.tasks.TaskRepository;
import org.github.olloginov.ideataskbrowser.TaskBrowserBundle;
import org.github.olloginov.ideataskbrowser.model.TaskSearch;
import org.github.olloginov.ideataskbrowser.model.TaskSearchList;
import org.jetbrains.annotations.NotNull;

public class ImportNewSearchesTask extends Task.Modal {
    private final TaskSearchList list;

    public ImportNewSearchesTask(@NotNull Project project, @NotNull TaskSearchList list) {
        super(project, TaskBrowserBundle.message("FetchNewIssuesTask.title"), true);
        this.list = list;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        TaskManager taskManager = TaskManager.getManager(myProject);
        for (TaskRepository r : taskManager.getAllRepositories()) {
            TaskSearch search = list.findSearchByRepository(r.getPresentableName());
            if (search == null) {
                search = new TaskSearch();
                search.setQuery("");
                search.setRepository(r.getPresentableName());
                list.add(search);
            }
        }
        list.updateIcons(taskManager);
    }
}
