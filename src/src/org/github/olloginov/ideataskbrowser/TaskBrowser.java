package org.github.olloginov.ideataskbrowser;

import com.intellij.openapi.components.PersistentStateComponent;
import org.github.olloginov.ideataskbrowser.config.TaskBrowserConfig;

import javax.swing.tree.TreeModel;

public interface TaskBrowser extends TaskBrowserServiceState, PersistentStateComponent<TaskBrowserConfig> {
    void refresh();

    void reloadChanges();

    TreeModel getFilteredModel();
}
