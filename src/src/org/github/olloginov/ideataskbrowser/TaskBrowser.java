package org.github.olloginov.ideataskbrowser;

import com.intellij.openapi.project.Project;
import com.intellij.tasks.Task;
import org.github.olloginov.ideataskbrowser.model.TaskSearchEventListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class TaskBrowser {
    @Nullable
    public static TaskBrowser getInstance(Project project) {
        return project == null ? null : project.getComponent(TaskBrowser.class);
    }

    public abstract void refreshAll();

    @Nullable
    public abstract Task getSelectedTask();

    public abstract void addListener(TaskSearchEventListener listener);

    public abstract void removeListener(TaskSearchEventListener listener);

    @NotNull
    public abstract Project getProject();

    @NotNull
    public abstract JComponent getPanel();
}
