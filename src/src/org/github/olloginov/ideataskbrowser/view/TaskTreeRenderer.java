package org.github.olloginov.ideataskbrowser.view;

import com.intellij.ui.ColoredTreeCellRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class TaskTreeRenderer extends ColoredTreeCellRenderer {
    @Override
    public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value instanceof CustomIcon) {
            setIcon(((CustomIcon) value).getIcon());
        }
        if (value instanceof CustomLabel) {
            ((CustomLabel) value).setLabel(this);
        }
    }
}
