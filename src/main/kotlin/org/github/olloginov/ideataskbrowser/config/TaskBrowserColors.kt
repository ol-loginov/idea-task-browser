package org.github.olloginov.ideataskbrowser.config

import com.intellij.application.options.colors.ColorAndFontOptions
import com.intellij.application.options.colors.ColorAndFontPanelFactory
import com.intellij.application.options.colors.ColorAndFontSettingsListener
import com.intellij.application.options.colors.NewColorAndFontPanel
import com.intellij.application.options.colors.OptionsPanelImpl
import com.intellij.application.options.colors.SchemesPanel
import com.intellij.openapi.editor.colors.ColorKey
import com.intellij.openapi.editor.colors.EditorSchemeAttributeDescriptor
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorAndFontDescriptorsProvider
import com.intellij.openapi.options.colors.ColorDescriptor
import org.github.olloginov.ideataskbrowser.TaskBrowserBundle

object TaskBrowserTheme {
	var WTB_TREE_BACKGROUND_COLOR = ColorKey.createColorKey("WTB_TREE_BACKGROUND_COLOR")
	var WTB_TREE_FOREGROUND_COLOR = ColorKey.createColorKey("WTB_TREE_FOREGROUND_COLOR")
	var WTB_PREVIEW_BACKGROUND_COLOR = ColorKey.createColorKey("WTB_PREVIEW_BACKGROUND_COLOR")
	var WTB_PREVIEW_FOREGROUND_COLOR = ColorKey.createColorKey("WTB_PREVIEW_FOREGROUND_COLOR")

	fun sectionGroup(): String = TaskBrowserBundle.message("settings.sectionName")
}

class TaskBrowserColors : ColorAndFontPanelFactory, ColorAndFontDescriptorsProvider {
	override fun getDisplayName(): String {
		return TaskBrowserTheme.sectionGroup()
	}

	override fun getAttributeDescriptors(): Array<AttributesDescriptor> = emptyArray()

	override fun getColorDescriptors(): Array<ColorDescriptor> = arrayOf(
		ColorDescriptor(TaskBrowserBundle.message("options.colors.descriptor.treeBackground"), TaskBrowserTheme.WTB_TREE_BACKGROUND_COLOR, ColorDescriptor.Kind.BACKGROUND),
		ColorDescriptor(TaskBrowserBundle.message("options.colors.descriptor.treeForeground"), TaskBrowserTheme.WTB_TREE_FOREGROUND_COLOR, ColorDescriptor.Kind.FOREGROUND),
		ColorDescriptor(TaskBrowserBundle.message("options.colors.descriptor.previewBackground"), TaskBrowserTheme.WTB_PREVIEW_BACKGROUND_COLOR, ColorDescriptor.Kind.BACKGROUND),
		ColorDescriptor(TaskBrowserBundle.message("options.colors.descriptor.previewForeground"), TaskBrowserTheme.WTB_PREVIEW_FOREGROUND_COLOR, ColorDescriptor.Kind.FOREGROUND)
	)

	override fun getPanelDisplayName(): String {
		return TaskBrowserTheme.sectionGroup()
	}

	override fun createPanel(options: ColorAndFontOptions): NewColorAndFontPanel {
		val schemesPanel = SchemesPanel(options)
		val optionsPanel = OptionsPanelImpl(options, schemesPanel, TaskBrowserTheme.sectionGroup())
		val previewPanel = TaskBrowserColorsPreviewPanel()

		optionsPanel.addListener(object : ColorAndFontSettingsListener.Abstract() {
			override fun selectedOptionChanged(selected: Any) {
				if (selected is EditorSchemeAttributeDescriptor) {
					previewPanel.setColorScheme(selected.scheme)
				}
			}

			override fun schemeChanged(source: Any) {
				previewPanel.setColorScheme(options.selectedScheme)
			}
		})

		return NewColorAndFontPanel(schemesPanel, optionsPanel, previewPanel, TaskBrowserTheme.sectionGroup(), null, null)
	}
}
