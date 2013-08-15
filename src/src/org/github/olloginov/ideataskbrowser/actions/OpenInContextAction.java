package org.github.olloginov.ideataskbrowser.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.tasks.LocalTask;
import com.intellij.tasks.Task;
import com.intellij.tasks.TaskManager;

public class OpenInContextAction extends AnActionImpl {
    public static final String ID = "OpenInContext";

    public OpenInContextAction() {
        super(ID);
    }

    @Override
    protected boolean isEnabled(Project project) {
        return getSelectedTask(project) != null;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            activateLocalTask(project);
        }
    }

    private void activateLocalTask(Project project) {
        Task task = getSelectedTask(project);
        if (task == null) {
            return;
        }

        TaskManager taskManager = TaskManager.getManager(project);
        LocalTask localTask = taskManager.activateTask(task, true);
        taskManager.performVcsOperation(localTask, TaskManager.VcsOperation.CREATE_CHANGELIST);
    }
}
