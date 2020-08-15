package org.github.olloginov.ideataskbrowser.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.openapi.actionSystem.impl.ActionButton;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.tasks.TaskState;
import org.github.olloginov.ideataskbrowser.TaskBrowser;
import org.github.olloginov.ideataskbrowser.TaskBrowserBundle;
import org.github.olloginov.ideataskbrowser.TaskBrowserServiceState;
import org.github.olloginov.ideataskbrowser.view.TaskBrowserPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class SetIssueFilterAction extends AnAction implements CustomComponentAction {
//    private final TaskBrowserServiceState serviceState;

    public SetIssueFilterAction() {
        super(TaskBrowserBundle.message("action.SetIssueFilterAction.description"), null, AllIcons.General.Filter);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        JComponent button = (JComponent) presentation.getClientProperty("button");
        if (button == null) {
            return;
        }

        DefaultActionGroup group = createPopupActionGroup(e.getProject());
        ActionManager.getInstance()
                .createActionPopupMenu(ActionPlaces.TODO_VIEW_TOOLBAR, group)
                .getComponent()
                .show(button, button.getWidth(), 0);
    }

    @NotNull
    @Override
    public JComponent createCustomComponent(@NotNull Presentation presentation, @NotNull String place) {
        ActionButton button = new ActionButton(this, presentation, TaskBrowserPanel.TOOL_WINDOW_ID, ActionToolbar.DEFAULT_MINIMUM_BUTTON_SIZE);
        presentation.putClientProperty("button", button);
        return button;
    }

    private static DefaultActionGroup createPopupActionGroup(Project project) {
        TaskBrowser taskBrowser = ServiceManager.getService(project, TaskBrowser.class);
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new TaskFilterApplier(TaskBrowserBundle.message("stateFilter.NA"), TaskBrowserBundle.message("stateFilter.NA.description"), null, taskBrowser));
        group.addSeparator();
        for (TaskState filterState : TaskState.values()) {
            group.add(new TaskFilterApplier(
                    TaskBrowserBundle.message("stateFilter." + filterState.name()),
                    TaskBrowserBundle.message("stateFilter." + filterState.name() + ".description"),
                    filterState, taskBrowser));
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
        public boolean isSelected(@NotNull AnActionEvent e) {
            return serviceState.isFilterEnabled(taskState);
        }

        @Override
        public void setSelected(@NotNull AnActionEvent e, boolean state) {
            serviceState.setFilterEnabled(taskState, state);
        }
    }
}
