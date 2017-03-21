package org.github.olloginov.ideataskbrowser.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
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
        TaskBrowser browser = TaskBrowser.getInstance(e.getProject());
        if (browser != null) {
            browser.refresh();
        }
    }
}
