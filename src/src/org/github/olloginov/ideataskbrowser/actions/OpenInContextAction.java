package org.github.olloginov.ideataskbrowser.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.tasks.Task;
import com.intellij.tasks.actions.OpenTaskDialog;
import org.github.olloginov.ideataskbrowser.TaskBrowserToolWindow;

public class OpenInContextAction extends AnActionImpl {
    private static final String ID = "OpenInContext";

    private final TaskBrowserToolWindow toolWindow;

    public OpenInContextAction(TaskBrowserToolWindow toolWindow) {
        super(ID);
        this.toolWindow = toolWindow;
    }

    @Override
    protected boolean isEnabled(Project project) {
        return toolWindow.getSelectedTask() != null;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        Task task = toolWindow.getSelectedTask();
        if (task == null) {
            return;
        }

        new OpenTaskDialog(e.getProject(), task).show();
    }
}
