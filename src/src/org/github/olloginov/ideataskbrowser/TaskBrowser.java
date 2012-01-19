package org.github.olloginov.ideataskbrowser;

import com.intellij.openapi.project.Project;
import com.intellij.tasks.Task;

public interface TaskBrowser {
    void updateList(Project project);

    Task getSelectedTask();
}
