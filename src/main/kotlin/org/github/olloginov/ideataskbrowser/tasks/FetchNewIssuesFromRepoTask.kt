package org.github.olloginov.ideataskbrowser.tasks

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.Project
import com.intellij.tasks.Task
import com.intellij.tasks.TaskManager
import com.intellij.tasks.TaskRepository
import org.github.olloginov.ideataskbrowser.TaskBrowserBundle
import org.github.olloginov.ideataskbrowser.TaskBrowserNotifier
import org.github.olloginov.ideataskbrowser.exceptions.RepositoryException
import org.github.olloginov.ideataskbrowser.model.TaskSearch
import org.github.olloginov.ideataskbrowser.view.TaskSearchTreeNode
import org.github.olloginov.ideataskbrowser.view.TaskTreeNode
import org.github.olloginov.ideataskbrowser.view.TreeNodeRef
import java.lang.reflect.InvocationTargetException
import javax.swing.SwingUtilities

private val logger = Logger.getInstance(FetchNewIssuesFromRepoTask::class.java)
private const val FETCH_ISSUES_BUFFER_SIZE: Int = 1024

private data class Page(
    val offset: Int,
    val size: Int
)

typealias BackgroundableSuper = com.intellij.openapi.progress.Task.Backgroundable

class FetchNewIssuesFromRepoTask(
    project: Project,
    private val searchNodeRef: TreeNodeRef<TaskSearchTreeNode>
) : BackgroundableSuper(project, TaskBrowserBundle.message("FetchNewIssuesFromRepoTask.title", searchNodeRef.node.getSearch().getRepository()), true) {

    private val notifier: TaskBrowserNotifier = ServiceManager.getService(TaskBrowserNotifier::class.java)

    private fun getNode(): TaskSearchTreeNode = searchNodeRef.node
    private fun getSearch(): TaskSearch = getNode().getSearch()

    private class FetchContext(
        val indicator: ProgressIndicator,
        val repository: TaskRepository
    ) {
        var addedCount = 0
        var updatedCount = 0
    }

    override fun run(indicator: ProgressIndicator) {
        val search = getSearch()

        val manager = TaskManager.getManager(myProject)
        val repository = manager.allRepositories.find { taskRepository -> search.getRepository() == taskRepository.presentableName }

        if (repository == null || indicator.isCanceled) {
            return
        }

        // don't run update twice
        if (!search.getUpdating().compareAndSet(false, true)) {
            return
        }

        try {
            val ctx = FetchContext(indicator, repository)
            importNew(ctx)
            updateCurrent(ctx)
        } finally {
            search.getUpdating().set(false)
        }
    }

    private fun importNew(ctx: FetchContext) {
        val title = TaskBrowserBundle.message("task.FetchNewIssuesTask.title", ctx.repository.presentableName)
        try {
            fetchAll(ctx)
            if (ctx.addedCount > 0 && ctx.updatedCount > 0) {
                if (ctx.addedCount == ctx.updatedCount) {
                    notifier.info(title, TaskBrowserBundle.message("task.FetchNewIssuesTask.finishing.added", ctx.addedCount, pluralizeTasks(ctx.addedCount)))
                } else {
                    notifier.info(title, TaskBrowserBundle.message("task.FetchNewIssuesTask.finishing.addedAndUpdated", ctx.addedCount, pluralizeTasks(ctx.addedCount), ctx.updatedCount))
                }
            } else if (ctx.addedCount > 0) {
                notifier.info(title, TaskBrowserBundle.message("task.FetchNewIssuesTask.finishing.added", ctx.addedCount, pluralizeTasks(ctx.addedCount)))
            } else if (ctx.updatedCount > 0) {
                notifier.info(title, TaskBrowserBundle.message("task.FetchNewIssuesTask.finishing.updated", ctx.updatedCount, pluralizeTasks(ctx.updatedCount)))
            } else {
                notifier.info(title, TaskBrowserBundle.message("task.FetchNewIssuesTask.finishing.noIssues"))
            }
        } catch (e: Exception) {
            notifier.error(title, TaskBrowserBundle.message("task.FetchNewIssuesTask.fetchError", e.message ?: ""))
            if (e !is RepositoryException) {
                logger.error(e)
            }
        }
    }

    private fun pluralizeTasks(count: Int): String {
        return TaskBrowserBundle.message("task" + (if (count > 1) ".many" else ".1"))
    }

    @Throws(exceptionClasses = [RepositoryException::class, InvocationTargetException::class, InterruptedException::class])
    private fun fetchAll(ctx: FetchContext) {
        val name = ctx.repository.presentableName
        ctx.indicator.text = TaskBrowserBundle.message("task.FetchNewIssuesTask.starting", name)

        val fetchQuery = getNode().getSearch().getQuery()

        val tasks = fetchChanges(ctx, Page(0, FETCH_ISSUES_BUFFER_SIZE), fetchQuery)
        if (tasks.isEmpty()) {
            return
        }

        tasks.forEach { task ->
            val taskNodeIndex = getNode().findTaskNode(task)
            // if this is new task
            if (taskNodeIndex < 0) {

                // but we don't want to display closed tasks
                if (!task.isClosed) {
                    // ok, proceed
                    ctx.addedCount++

                    // node not found, but got place where insert
                    val insertAt = -(taskNodeIndex + 1)
                    SwingUtilities.invokeAndWait { searchNodeRef.insertChild(insertAt, TaskTreeNode(task)) }
                }
            }
            // else for existing task if it's been closed
            else if (task.isClosed) {
                // NB: maybe here we should update and display ctx.removedCount
                ctx.updatedCount++
                SwingUtilities.invokeAndWait { searchNodeRef.removeChild(taskNodeIndex) }
            }
            // else for all existing task
            else {
                ctx.updatedCount++
            }
        }
    }

    @Throws(RepositoryException::class)
    private fun fetchChanges(ctx: FetchContext, page: Page, fetchQuery: String): Array<Task> {
        try {
            return ctx.repository.getIssues(fetchQuery, page.offset, page.size, false, ctx.indicator)
        } catch (e: Exception) {
            throw RepositoryException(TaskBrowserBundle.message("error.connection.broken") + ": " + e.message, e)
        }
    }

    private fun updateTask(ctx: FetchContext, task: Task): Task? {
        return try {
            ctx.repository.findTask(task.id)
        } catch (e: Exception) {
            logger.error(e)
            null
        }
    }

    private fun updateCurrent(ctx: FetchContext) {
        val childCount = getNode().childCount
        for (index in 0 until childCount) {
            ctx.indicator.isIndeterminate = false
            ctx.indicator.fraction = index / childCount.toDouble()

            val taskNode = getNode().getChildAt(index)
            val task = updateTask(ctx, taskNode.getTask()) ?: continue
            taskNode.setTask(task)
            SwingUtilities.invokeLater { searchNodeRef.updateChild(taskNode) }
        }
    }
}
