package org.github.olloginov.ideataskbrowser.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.openapi.actionSystem.impl.ActionButton;
import com.intellij.openapi.project.Project;
import com.intellij.tasks.TaskState;
import org.github.olloginov.ideataskbrowser.TaskBrowserBundle;
import org.github.olloginov.ideataskbrowser.TaskBrowserServiceState;
import org.github.olloginov.ideataskbrowser.view.TaskBrowserPanel;

import javax.swing.*;

@SuppressWarnings("ComponentNotRegistered")
public class SetIssueFilterAction extends AnAction implements CustomComponentAction {
    private final Project project;
    private final TaskBrowserServiceState serviceState;

    public SetIssueFilterAction(Project project, TaskBrowserServiceState serviceState) {
        super(TaskBrowserBundle.message("action.SetIssueFilterAction.description"), null, AllIcons.General.Filter);
        this.project = project;
        this.serviceState = serviceState;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        JComponent button = (JComponent) presentation.getClientProperty("button");
        DefaultActionGroup group = createPopupActionGroup(serviceState);
        ActionManager.getInstance()
                .createActionPopupMenu(ActionPlaces.TODO_VIEW_TOOLBAR, group)
                .getComponent()
                .show(button, button.getWidth(), 0);
    }

    @Override
    public JComponent createCustomComponent(Presentation presentation) {
        ActionButton button = new ActionButton(this, presentation, TaskBrowserPanel.TOOL_WINDOW_ID, ActionToolbar.DEFAULT_MINIMUM_BUTTON_SIZE);
        presentation.putClientProperty("button", button);
        return button;
    }

    public static DefaultActionGroup createPopupActionGroup(TaskBrowserServiceState serviceState) {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new TaskFilterApplier(TaskBrowserBundle.message("stateFilter.NA"), TaskBrowserBundle.message("stateFilter.NA.description"), null, serviceState));
        group.addSeparator();
        for (TaskState filterState : TaskState.values()) {
            group.add(new TaskFilterApplier(
                    TaskBrowserBundle.message("stateFilter." + filterState.name()),
                    TaskBrowserBundle.message("stateFilter." + filterState.name() + ".description"),
                    filterState, serviceState));
        }
        return group;
    }

    private static class TaskFilterApplier extends ToggleAction {
        private final TaskState taskState;
        private final TaskBrowserServiceState serviceState;

        TaskFilterApplier(String text, String description, TaskState taskState, TaskBrowserServiceState serviceState) {
            super(null, description, null);
            getTemplatePresentation().setText(text, false);
            this.taskState = taskState;
            this.serviceState = serviceState;
        }

        @Override
        public boolean isSelected(AnActionEvent e) {
            return serviceState.isFilterEnabled(taskState);
        }

        @Override
        public void setSelected(AnActionEvent e, boolean state) {
            serviceState.setFilterEnabled(taskState, state);
        }
    }
}
