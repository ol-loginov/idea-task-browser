package org.github.olloginov.ideataskbrowser.tasks;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.tasks.TaskManager;
import com.intellij.tasks.TaskRepository;
import org.github.olloginov.ideataskbrowser.TaskBrowserBundle;
import org.github.olloginov.ideataskbrowser.TaskBrowserService;
import org.jetbrains.annotations.NotNull;

public class UpdateRepositoriesTask extends Task.Modal {
    private final TaskBrowserService service;

    public UpdateRepositoriesTask(@NotNull TaskBrowserService service) {
        super(service.getProject(), TaskBrowserBundle.message("UpdateRepositoriesTask.title"), true);
        this.service = service;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        TaskManager taskManager = TaskManager.getManager(myProject);
        for (TaskRepository repository : taskManager.getAllRepositories()) {

        }
    }
}
