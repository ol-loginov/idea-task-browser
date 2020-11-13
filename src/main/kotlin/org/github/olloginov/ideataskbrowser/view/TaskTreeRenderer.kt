package org.github.olloginov.ideataskbrowser.view

import com.intellij.ui.ColoredTreeCellRenderer
import javax.swing.JTree

class TaskTreeRenderer : ColoredTreeCellRenderer() {
	override fun customizeCellRenderer(tree: JTree, value: Any?, selected: Boolean, expanded: Boolean, leaf: Boolean, row: Int, hasFocus: Boolean) {
		if (value is CustomIcon) {
			setIcon(value.getIcon())
		}
		if (value is CustomLabel) {
			value.setLabel(this)
		}
	}
}
