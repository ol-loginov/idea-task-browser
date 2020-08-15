package org.github.olloginov.ideataskbrowser;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.ContentManager;
import org.github.olloginov.ideataskbrowser.view.TaskBrowserPanel;
import org.jetbrains.annotations.NotNull;

public class ToolWindowFactory implements com.intellij.openapi.wm.ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, ToolWindow toolWindow) {
        ContentManager cm = toolWindow.getContentManager();

        TaskBrowser service = ServiceManager.getService(project, TaskBrowser.class);
        if (service == null) {
            throw new IllegalStateException("Task browser service not ready");
        }

        TaskBrowserPanel taskTreePanel = new TaskBrowserPanel(project);
        taskTreePanel.setTreeModel(service.getFilteredModel());

        cm.addContent(cm.getFactory().createContent(taskTreePanel.wrapInToolWindowPanel(), null, false));
    }
}
