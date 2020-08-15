package org.github.olloginov.ideataskbrowser.actions;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.tasks.Task;
import org.github.olloginov.ideataskbrowser.TaskBrowserToolWindow;
import org.jetbrains.annotations.NotNull;

public class OpenInBrowserAction extends AnActionImpl {
    private static final String ID = "OpenInBrowser";

    private final TaskBrowserToolWindow toolWindow;

    public OpenInBrowserAction(TaskBrowserToolWindow toolWindow) {
        super(ID);
        this.toolWindow = toolWindow;
    }

    @Override
    protected boolean isEnabled(Project project) {
        return getIssueUrl() != null;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String taskUrl = getIssueUrl();
        if (taskUrl != null) {
            BrowserUtil.browse(taskUrl);
        }
    }

    private String getIssueUrl() {
        Task task = toolWindow.getSelectedTask();
        if (task == null) {
            return null;
        }

        String taskUrl = task.getIssueUrl();
        if (taskUrl != null && taskUrl.length() > 0) {
            return taskUrl;
        }

        return null;
    }
}
