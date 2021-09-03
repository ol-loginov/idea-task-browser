package org.github.olloginov.ideataskbrowser.config

import com.intellij.application.options.colors.ColorAndFontSettingsListener
import com.intellij.application.options.colors.PreviewPanel
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.tasks.TaskRepositoryType
import com.intellij.tasks.TaskType
import com.intellij.tasks.impl.LocalTaskImpl
import org.github.olloginov.ideataskbrowser.TaskBrowserBundle
import org.github.olloginov.ideataskbrowser.model.TaskSearch
import org.github.olloginov.ideataskbrowser.model.TaskSearchList
import org.github.olloginov.ideataskbrowser.view.TaskBrowserPanel
import org.github.olloginov.ideataskbrowser.view.TaskSearchTreeNode
import org.github.olloginov.ideataskbrowser.view.TaskTreeModel
import org.github.olloginov.ideataskbrowser.view.TaskTreeNode
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.tree.MutableTreeNode
import kotlin.random.Random

private val RANDOM = Random(System.currentTimeMillis())
private fun randomTaskType(): TaskType {
    val enumValues = TaskType.values()
    return enumValues[RANDOM.nextInt(enumValues.size)]
}

private const val PREVIEW_PANEL_PADDING = 10

class TaskBrowserColorsPreviewPanel : PreviewPanel {
    private var container: JPanel? = null
    private var panel: TaskBrowserPanel? = null

    override fun getPanel(): Component {
        val panel = this.panel ?: TaskBrowserPanel(null).also {
            it.root.border = BorderFactory.createEmptyBorder(PREVIEW_PANEL_PADDING, 0, PREVIEW_PANEL_PADDING, PREVIEW_PANEL_PADDING)

            val repositories = TaskSearchList()
            TaskRepositoryType.getRepositoryTypes()
                .map { repositoryType -> repositoryType to TaskSearch() }
                .onEach { pair -> pair.second.setRepository(pair.first.name) }
                .onEach { pair -> pair.second.setIcon(pair.first.icon) }
                .map { pair -> pair.second }
                .forEach { repository -> repositories.add(repository) }

            val treeModel = TaskTreeModel(repositories)
            for (child in treeModel.root.children()) {
                val repoNode = child as TaskSearchTreeNode

                val task = LocalTaskImpl("", "${repoNode.getSearch().getRepository()} Issue")
                task.type = randomTaskType()
                treeModel.insertNodeInto(TaskTreeNode(task), child as MutableTreeNode, 0)
            }
            it.setTreeModel(treeModel)

            this.panel = it
        }
        return this.container ?: JPanel(BorderLayout(0, 0)).also {
            it.border = BorderFactory.createTitledBorder(TaskBrowserBundle.message("options.colors.preview"))
            it.add(panel.root, BorderLayout.CENTER)

            this.container = it
        }
    }

    override fun disposeUIResources() {
        container = null
        panel = null
    }

    override fun updateView() = Unit

    fun setColorScheme(scheme: EditorColorsScheme?) {
        val panel = this.panel ?: return
        if (scheme != null) {
            panel.setColorScheme(scheme)
        }
    }

    override fun blinkSelectedHighlightType(selected: Any?) = Unit

    override fun addListener(listener: ColorAndFontSettingsListener) = Unit
}
