package org.github.olloginov.ideataskbrowser.tasks

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.Project
import com.intellij.tasks.TaskManager
import org.github.olloginov.ideataskbrowser.TaskBrowserBundle
import org.github.olloginov.ideataskbrowser.model.TaskSearch
import org.github.olloginov.ideataskbrowser.model.TaskSearchList
import org.github.olloginov.ideataskbrowser.view.TaskTreeModel

class UpdateRepositoriesTask(
	project: Project,
	private val list: TaskSearchList,
	private val tree: TaskTreeModel
) : com.intellij.openapi.progress.Task.Backgroundable(project, TaskBrowserBundle.message("UpdateRepositoriesTask.title"), true) {

	override fun run(indicator: ProgressIndicator) {
		val taskManager = TaskManager.getManager(myProject)

		taskManager.allRepositories.forEach { r ->
			var search = list.findSearchByRepository(r.presentableName)
			if (search == null) {
				indicator.text = TaskBrowserBundle.message("UpdateRepositoriesTask.updateRepository", r.presentableName)
				search = TaskSearch()
				search.setQuery("")
				search.setRepository(r.presentableName)
				list.add(search)
			}
		}

		indicator.text = TaskBrowserBundle.message("UpdateRepositoriesTask.title")
		list.updateIcons(taskManager)

		FetchNewIssuesTask(project, tree).run(indicator)
	}
}
