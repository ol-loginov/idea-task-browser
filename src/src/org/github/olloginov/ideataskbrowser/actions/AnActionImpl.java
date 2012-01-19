package org.github.olloginov.ideataskbrowser.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import org.github.olloginov.ideataskbrowser.TaskBrowserBundle;

import javax.swing.*;

public abstract class AnActionImpl extends AnAction {
    public AnActionImpl(String key) {
        super(resolveText(key), resolveDescription(key), resolveIcon(key));
    }

    @Override
    public void update(AnActionEvent e) {
        Project project = e.getProject();
        e.getPresentation().setEnabled(project != null && isEnabled(project));
    }

    protected boolean isEnabled(Project project) {
        return false;
    }

    private static Icon resolveIcon(String key) {
        return IconLoader.getIcon("/org/github/olloginov/ideataskbrowser/action." + key + ".png");
    }

    private static String resolveDescription(String key) {
        return TaskBrowserBundle.message("action." + key + ".description");
    }

    private static String resolveText(String key) {
        return TaskBrowserBundle.message("action." + key + ".text");
    }
}
