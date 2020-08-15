package org.github.olloginov.ideataskbrowser;

import com.intellij.tasks.Task;
import org.jetbrains.annotations.Nullable;

public interface TaskBrowserToolWindow {
    @Nullable
    Task getSelectedTask();
}
