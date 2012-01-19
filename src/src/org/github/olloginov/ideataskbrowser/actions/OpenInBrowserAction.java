package org.github.olloginov.ideataskbrowser.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.github.olloginov.ideataskbrowser.TaskBrowser;

public class OpenInBrowserAction extends AnActionImpl {
    public OpenInBrowserAction() {
        super("OpenInBrowser");
    }

    @Override
    protected boolean isEnabled(Project project) {
        TaskBrowser taskBrowser = ServiceManager.getService(project, TaskBrowser.class);
        return taskBrowser.getSelectedTask() != null;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
    }
}
