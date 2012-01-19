package org.github.olloginov.ideataskbrowser;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;

public class ToolWindowFactory implements com.intellij.openapi.wm.ToolWindowFactory {
    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        TaskBrowserWindow service = (TaskBrowserWindow) getTaskBrowserInstance(project);

        final ContentManager cm = toolWindow.getContentManager();
        final Content content = cm.getFactory().createContent(service, null, false);
        cm.addContent(content);
    }

    public static TaskBrowser getTaskBrowserInstance(Project project) {
        return ServiceManager.getService(project, TaskBrowser.class);
    }
}
