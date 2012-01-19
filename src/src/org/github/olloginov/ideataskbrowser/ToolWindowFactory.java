package org.github.olloginov.ideataskbrowser;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.ContentManager;

public class ToolWindowFactory implements com.intellij.openapi.wm.ToolWindowFactory {
    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        ContentManager cm = toolWindow.getContentManager();

        TaskBrowser service = TaskBrowser.getInstance(project);
        if (service == null) {
            throw new IllegalStateException("Task browser service not ready");
        }
        cm.addContent(cm.getFactory().createContent(service.getPanel(), null, false));
    }
}
