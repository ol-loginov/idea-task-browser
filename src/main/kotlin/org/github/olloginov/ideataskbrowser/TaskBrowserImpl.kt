package org.github.olloginov.ideataskbrowser

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.tasks.TaskManager
import com.intellij.tasks.TaskState
import com.intellij.util.xmlb.XmlSerializerUtil
import org.github.olloginov.ideataskbrowser.config.TaskBrowserConfig
import org.github.olloginov.ideataskbrowser.model.TaskSearch
import org.github.olloginov.ideataskbrowser.model.TaskSearchList
import org.github.olloginov.ideataskbrowser.tasks.FetchNewIssuesTask
import org.github.olloginov.ideataskbrowser.tasks.UpdateRepositoriesTask
import org.github.olloginov.ideataskbrowser.view.TaskTreeModel
import org.github.olloginov.ideataskbrowser.view.TaskTreeModelWithFilter
import java.util.*
import javax.swing.SwingUtilities
import javax.swing.tree.TreeModel

private fun isDiffer(a: TaskSearchList, b: Iterable<TaskSearch>): Boolean {
	val stringer = { v: TaskSearch -> String.format("%s:%s", v.getRepository(), v.getQuery()) }
	val listStringer = { list: Iterable<TaskSearch> -> list.joinToString("\n", transform = stringer) }
	return listStringer(a.getInnerList()) != listStringer(b)
}

@State(name = "TaskBrowser", storages = [Storage(StoragePathMacros.WORKSPACE_FILE)])
class TaskBrowserImpl(
	private val project: Project
) : TaskBrowser, TaskBrowserServiceState {

	private val searchList = TaskSearchList()

	private val searchFilters = mutableListOf<TaskState?>()
	private var doubleClickAction = TaskBrowserConfig.DoubleClickAction.NOTHING

	private val taskTreeModel = TaskTreeModel(searchList)
	private val taskTreeModelWithFilter = TaskTreeModelWithFilter(taskTreeModel, getEnabledFilters())

	override fun getFilteredModel(): TreeModel {
		return taskTreeModelWithFilter
	}

	@Override
	override fun getState(): TaskBrowserConfig {
		val config = TaskBrowserConfig()
		config.doubleClickAction = doubleClickAction

		for (index in 0 until searchList.size) {
			config.searches.add(searchList.getElementAt(index))
		}

		getEnabledFilters().forEach { filter: TaskState? ->
			config.filters.add(filter?.name ?: "")
		}
		return config
	}

	@Override
	override fun loadState(state: TaskBrowserConfig) {
		val taskManager = TaskManager.getManager(project)
		val repositories = taskManager.allRepositories

		val config = TaskBrowserConfig()
		XmlSerializerUtil.copyBean(state, config)

		// remove obsolete searches
		config.searches.removeIf { search ->
			repositories.none { repository -> search.getRepository() == repository.presentableName }
		}

		if (isDiffer(searchList, config.searches)) {
			searchList.clear()

			config.searches.forEach { search ->
				searchList.add(search)
			}
			searchList.updateIcons(taskManager)
		}

		doubleClickAction = config.doubleClickAction

		config.filters.forEach { filter ->
			if (filter.isEmpty()) {
				setFilterEnabled(null, true)
			} else {
				try {
					setFilterEnabled(TaskState.valueOf(filter), true)
				} catch (e: IllegalArgumentException) {
					// just ignore wrong value
				}
			}
		}
	}

	override fun refresh() {
		ApplicationManager.getApplication().runReadAction {
			ProgressManager.getInstance().run(UpdateRepositoriesTask(project, searchList, taskTreeModel))
		}
	}

	override fun reloadChanges() {
		ApplicationManager.getApplication().runReadAction {
			taskTreeModelWithFilter.setStateFilter(getEnabledFilters())
			ProgressManager.getInstance().run(FetchNewIssuesTask(project, taskTreeModel))
		}
	}

	private fun getEnabledFilters(): List<TaskState?> = Collections.unmodifiableList(searchFilters)

	override fun isFilterEnabled(target: TaskState?): Boolean = searchFilters.contains(target)

	override fun setFilterEnabled(target: TaskState?, state: Boolean) {
		val enabled = isFilterEnabled(target)
		if (enabled == state) {
			return
		}

		if (state) {
			searchFilters.add(target)
		} else {
			searchFilters.remove(target)
		}

		SwingUtilities.invokeLater {
			taskTreeModelWithFilter.setStateFilter(getEnabledFilters())
		}
	}
}
