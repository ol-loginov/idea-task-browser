package org.github.olloginov.ideataskbrowser.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.ActionToolbar
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.actionSystem.ToggleAction
import com.intellij.openapi.actionSystem.ex.CustomComponentAction
import com.intellij.openapi.actionSystem.impl.ActionButton
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.tasks.TaskState
import org.github.olloginov.ideataskbrowser.TaskBrowser
import org.github.olloginov.ideataskbrowser.TaskBrowserBundle
import org.github.olloginov.ideataskbrowser.TaskBrowserServiceState
import org.github.olloginov.ideataskbrowser.view.TaskBrowserPanel
import javax.swing.JComponent

class SetIssueFilterAction(
    private val noop: Boolean
) : AnAction(TaskBrowserBundle.message("action.SetIssueFilterAction.description"), null, AllIcons.General.Filter),
    CustomComponentAction {

    override fun actionPerformed(e: AnActionEvent) {
        val presentation = e.presentation
        val button = presentation.getClientProperty("button") as JComponent? ?: return

        val group = createPopupActionGroup(e.project!!, noop)
        ActionManager.getInstance()
            .createActionPopupMenu(ActionPlaces.TODO_VIEW_TOOLBAR, group)
            .component
            .show(button, button.width, 0)
    }

    override fun createCustomComponent(presentation: Presentation, place: String): JComponent {
        val button = ActionButton(this, presentation, TaskBrowserPanel.TOOL_WINDOW_ID, ActionToolbar.DEFAULT_MINIMUM_BUTTON_SIZE)
        presentation.putClientProperty("button", button)
        return button
    }
}

private class TaskFilterApplier(
    text: String,
    description: String,
    private val taskState: TaskState?,
    private val serviceState: TaskBrowserServiceState,
    private val noop: Boolean
) : ToggleAction(null, description, null) {

    init {
        templatePresentation.setText(text, false)
    }

    override fun isSelected(e: AnActionEvent): Boolean {
        return serviceState.isFilterEnabled(taskState)
    }

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        if (noop) {
            return
        }
        serviceState.setFilterEnabled(taskState, state)
    }
}

private fun createPopupActionGroup(project: Project, noop: Boolean): DefaultActionGroup {
    val taskBrowser = ServiceManager.getService(project, TaskBrowser::class.java)
    val group = DefaultActionGroup()
    group.add(TaskFilterApplier(TaskBrowserBundle.message("stateFilter.NA"), TaskBrowserBundle.message("stateFilter.NA.description"), null, taskBrowser, noop))
    group.addSeparator()

    TaskState.values().forEach { filterState ->
        group.add(
            TaskFilterApplier(
                TaskBrowserBundle.message("stateFilter." + filterState.name),
                TaskBrowserBundle.message("stateFilter." + filterState.name + ".description"),
                filterState, taskBrowser, noop
            )
        )
    }
    return group
}
