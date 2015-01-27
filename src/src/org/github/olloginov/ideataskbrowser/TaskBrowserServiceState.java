package org.github.olloginov.ideataskbrowser;

import com.intellij.tasks.TaskState;

public interface TaskBrowserServiceState {
    boolean isFilterEnabled(TaskState target);

    void setFilterEnabled(TaskState target, boolean state);
}
