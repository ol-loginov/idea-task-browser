package org.github.olloginov.ideataskbrowser.view

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.tasks.Task
import com.intellij.ui.JBSplitter
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.treeStructure.Tree
import org.github.olloginov.ideataskbrowser.TaskBrowser
import org.github.olloginov.ideataskbrowser.TaskBrowserToolWindow
import org.github.olloginov.ideataskbrowser.actions.OpenInBrowserAction
import org.github.olloginov.ideataskbrowser.actions.OpenInContextAction
import org.github.olloginov.ideataskbrowser.actions.RefreshListAction
import org.github.olloginov.ideataskbrowser.actions.SetIssueFilterAction
import org.github.olloginov.ideataskbrowser.config.TaskBrowserConfig
import org.github.olloginov.ideataskbrowser.config.TaskBrowserTheme
import org.github.olloginov.ideataskbrowser.util.TaskHelper
import java.awt.BorderLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.text.DateFormat
import javax.swing.BorderFactory
import javax.swing.JEditorPane
import javax.swing.JPanel
import javax.swing.UIManager
import javax.swing.text.html.HTMLEditorKit
import javax.swing.tree.TreeModel
import javax.swing.tree.TreeNode
import javax.swing.tree.TreeSelectionModel

private fun htmlBreak(text: String?): String {
	if (text == null) return ""
	return text
		.replace("(\\r\\n|\\r|\\n)", "<br>")
}

private fun htmlSafe(text: String?): String {
	if (text == null) return ""
	return text
		.replace("<a", "&lt;a")
		.replace("<script", "&lt;script")
}

class TaskBrowserPanel(
	private val project: Project? = null
) : TaskBrowserToolWindow {
	companion object {
		const val TOOL_WINDOW_ID = "TaskBrowser"
	}

	private val previewHtml: JEditorPane = JEditorPane()
	private val tree: Tree = Tree()
	val root: JPanel = JPanel()

	val noop = project == null

	init {
		initComponent()

		previewHtml.editorKit = HTMLEditorKit()

		tree.selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION
		tree.cellRenderer = TaskTreeRenderer()

		setTreeModel(TaskTreeModel())
		initToolbarActions()

		tree.addMouseListener(object : MouseAdapter() {
			override fun mouseClicked(e: MouseEvent) {
				if (e.clickCount < 1) {
					return
				}
				if (e.clickCount == 1) {
					listenTreeSingleClick()
				} else {
					listenTreeDoubleClick(e)
				}
			}
		})
	}

	private fun initComponent() {
		previewHtml.border = BorderFactory.createEmptyBorder()
		previewHtml.contentType = "text/html"
		previewHtml.isEditable = false

		val scrollPane1 = ScrollPaneFactory.createScrollPane()
		scrollPane1.border = null
		scrollPane1.background = UIManager.getColor("EditorPane.selectionForeground")
		scrollPane1.foreground = UIManager.getColor("EditorPane.background")
		scrollPane1.setViewportView(previewHtml)

		val preview = JPanel()
		preview.layout = BorderLayout(10, 10)
		preview.background = UIManager.getColor("EditorPane.background")
		preview.add(scrollPane1, BorderLayout.CENTER)

		tree.border = null
		tree.isRootVisible = false
		tree.showsRootHandles = true

		val treeScroll = ScrollPaneFactory.createScrollPane()
		treeScroll.border = null
		treeScroll.setViewportView(tree)

		val contentSplitter = JBSplitter()
		contentSplitter.border = null
		contentSplitter.orientation = false
		contentSplitter.proportion = .5f
		contentSplitter.firstComponent = treeScroll
		contentSplitter.secondComponent = preview

		root.border = null
		root.layout = BorderLayout(0, 0)
		root.add(contentSplitter, BorderLayout.CENTER)
	}

	private fun initToolbarActions() {
		val toolbarGroup = DefaultActionGroup()
		toolbarGroup.add(RefreshListAction(noop))
		toolbarGroup.add(OpenInContextAction(this, noop))
		toolbarGroup.add(OpenInBrowserAction(this, noop))
		toolbarGroup.add(SetIssueFilterAction(noop))

		val actionManager = ActionManager.getInstance()
		val toolbar = actionManager.createActionToolbar(TOOL_WINDOW_ID, toolbarGroup, false)
		root.add(toolbar.component, BorderLayout.WEST)
	}

	fun wrapInToolWindowPanel(): SimpleToolWindowPanel {
		val panel = SimpleToolWindowPanel(false)
		panel.add(root, BorderLayout.CENTER)
		return panel
	}

	fun setTreeModel(treeModel: TreeModel) {
		this.tree.model = treeModel
	}

	private fun getSelectedNode(): TreeNode? {
		val selectionPath = tree.selectionPath ?: return null
		return selectionPath.lastPathComponent as TreeNode
	}

	override fun getSelectedTask(): Task? {
		val node = getSelectedNode()
		if (node is TaskTreeNode) {
			return node.getTask()
		}
		return null
	}

	private fun listenTreeSingleClick() {
		val task = getSelectedTask() ?: return

		val df = DateFormat.getDateTimeInstance()
		var dateString = ""
		if (TaskHelper.getChangeDate(task) != null) {
			dateString = df.format(TaskHelper.getChangeDate(task))
		}

		val html = StringBuilder("<html><body style='padding: 5px;'>")
			.append(String.format("<small>%s</small><br>", dateString))
			.append(String.format("<b>%s</b><br><br>", htmlSafe(task.presentableName)))
			.append(String.format("<p>%s</p><br><br><br>", if (task.description == null) "" else htmlBreak(task.description)))

		task.comments.forEach { comment ->
			html.append(String.format("<b>%s</b> <small>%s</small><br>%s<br>", df.format(comment.date), htmlSafe(comment.author), comment.text))
		}

		html.append("</body></html>")
		previewHtml.text = html.toString()
	}

	private fun listenTreeDoubleClick(e: MouseEvent) {
		if (project == null) {
			return
		}

		val taskBrowser = ServiceManager.getService(project, TaskBrowser::class.java) ?: return
		val config = taskBrowser.state ?: return

		when (config.doubleClickAction) {
			TaskBrowserConfig.DoubleClickAction.SWITCH_CONTEXT -> ActionManager.getInstance().tryToExecute(OpenInContextAction(this, noop), e, tree, null, true)
			TaskBrowserConfig.DoubleClickAction.OPEN_IN_BROWSER -> ActionManager.getInstance().tryToExecute(OpenInBrowserAction(this, noop), e, tree, null, true)
			else -> {
			}
		}
	}

	fun setColorScheme(scheme: EditorColorsScheme) {
		tree.background = scheme.getColor(TaskBrowserTheme.TASK_TREE_BACKGROUND_COLOR)
	}
}
