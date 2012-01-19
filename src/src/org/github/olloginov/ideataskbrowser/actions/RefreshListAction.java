package org.github.olloginov.ideataskbrowser.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.github.olloginov.ideataskbrowser.TaskBrowser;

public class RefreshListAction extends AnActionImpl {
    public RefreshListAction() {
        super("RefreshList");
    }

    @Override
    protected boolean isEnabled(Project project) {
        return true;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            TaskBrowser browser = ServiceManager.getService(project, TaskBrowser.class);
            if (browser != null) {
                browser.updateList(project);
            }
        }
    }
}
