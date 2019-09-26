package org.github.olloginov.ideataskbrowser.actions;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.tasks.Task;

public class OpenInBrowserAction extends AnActionImpl {
    private static final String ID = "OpenInBrowser";

    public OpenInBrowserAction() {
        super(ID);
    }

    @Override
    protected boolean isEnabled(Project project) {
        return getIssueUrl(project) != null;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        String taskUrl = getIssueUrl(e.getProject());
        if (taskUrl != null) {
            BrowserUtil.browse(taskUrl);
        }
    }

    private String getIssueUrl(Project project) {
        if (project != null) {
            Task task = getSelectedTask(project);
            if (task != null) {
                String taskUrl = task.getIssueUrl();
                if (taskUrl != null && taskUrl.length() > 0) {
                    return taskUrl;
                }
            }
        }
        return null;
    }
}
