package org.github.olloginov.ideataskbrowser.tasks

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import org.github.olloginov.ideataskbrowser.TaskBrowserBundle
import org.github.olloginov.ideataskbrowser.view.TaskTreeModel
import org.github.olloginov.ideataskbrowser.view.TreeNodeRef

class FetchNewIssuesTask(
    project: Project,
    private val treeModel: TaskTreeModel
) : com.intellij.openapi.progress.Task.Backgroundable(project, TaskBrowserBundle.message("fetchNewIssuesTask.title"), true) {

    override fun run(indicator: ProgressIndicator) {
        val rootTreeNode = treeModel.root
        for (index in 0 until rootTreeNode.childCount) {
            val target = TreeNodeRef(treeModel, rootTreeNode.getChildAt(index))
            ApplicationManager.getApplication().invokeLater {
                ProgressManager.getInstance().run(FetchNewIssuesFromRepoTask(project, target))
            }
        }
    }
}
