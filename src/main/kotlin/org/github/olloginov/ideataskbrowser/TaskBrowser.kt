package org.github.olloginov.ideataskbrowser;

import com.intellij.openapi.components.PersistentStateComponent;
import org.github.olloginov.ideataskbrowser.config.TaskBrowserConfig;

import javax.swing.tree.TreeModel;

interface TaskBrowser : TaskBrowserServiceState, PersistentStateComponent<TaskBrowserConfig> {
	fun refresh()

	fun reloadChanges()

	fun getFilteredModel(): TreeModel
}
