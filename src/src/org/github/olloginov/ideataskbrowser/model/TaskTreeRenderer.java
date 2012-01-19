package org.github.olloginov.ideataskbrowser.model;

import com.intellij.openapi.util.IconLoader;
import com.intellij.tasks.LocalTask;
import com.intellij.tasks.TaskType;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class TaskTreeRenderer extends DefaultTreeCellRenderer {
    private final static Icon BUG_ICON = IconLoader.getIcon("/icons/bug.png", LocalTask.class);
    private final static Icon EXCEPTION_ICON = IconLoader.getIcon("/icons/exception.png", LocalTask.class);
    private final static Icon FEATURE_ICON = IconLoader.getIcon("/icons/feature.png", LocalTask.class);
    private final static Icon OTHER_ICON = IconLoader.getIcon("/icons/other.png", LocalTask.class);
    private final static Icon UNKNOWN_ICON = IconLoader.getIcon("/icons/unknown.png", LocalTask.class);

    public static Icon getIconByType(TaskType taskType) {
        if (taskType == null) {
            return UNKNOWN_ICON;
        }
        switch (taskType) {
            case BUG:
                return BUG_ICON;
            case EXCEPTION:
                return EXCEPTION_ICON;
            case FEATURE:
                return FEATURE_ICON;
            default:
                return OTHER_ICON;
        }
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if (value instanceof CustomIcon) {
            setIcon(((CustomIcon) value).getIcon());
        }
        return c;
    }
}
