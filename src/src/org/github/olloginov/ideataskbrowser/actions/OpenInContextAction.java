package org.github.olloginov.ideataskbrowser.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.tasks.Task;
import com.intellij.tasks.TaskManager;

public class OpenInContextAction extends AnActionImpl {
    public OpenInContextAction() {
        super("OpenInContext");
    }

    @Override
    protected boolean isEnabled(Project project) {
        return getSelectedTask(project) != null;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        Task task = getSelectedTask(project);
        if (task == null) {
            return;
        }

        TaskManager.getManager(project).activateTask(task, true, true);
    }
}
