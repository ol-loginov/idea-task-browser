package org.github.olloginov.ideataskbrowser;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

public class TaskBrowserPostStartupActivity implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        TaskBrowser taskBrowser = ServiceManager.getService(project, TaskBrowser.class);
        if (taskBrowser != null) {
            taskBrowser.refresh();
        }
    }
}
