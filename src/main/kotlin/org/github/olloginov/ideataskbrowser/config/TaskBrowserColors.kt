package org.github.olloginov.ideataskbrowser.config

import com.intellij.application.options.colors.*
import com.intellij.openapi.editor.colors.ColorKey
import com.intellij.openapi.editor.colors.EditorSchemeAttributeDescriptor
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorAndFontDescriptorsProvider
import com.intellij.openapi.options.colors.ColorDescriptor
import org.github.olloginov.ideataskbrowser.TaskBrowserBundle

object TaskBrowserTheme {
	var WTB_PANE_CONTENT_BACKGROUND_COLOR = ColorKey.createColorKey("WTB_PANE_CONTENT_BACKGROUND_COLOR")
	var WTB_PANE_CONTENT_FOREGROUND_COLOR = ColorKey.createColorKey("WTB_PANE_CONTENT_FOREGROUND_COLOR")

	fun sectionGroup(): String = TaskBrowserBundle.message("settings.sectionName")
}

class TaskBrowserColors : ColorAndFontPanelFactory, ColorAndFontDescriptorsProvider {
	override fun getDisplayName(): String {
		return TaskBrowserTheme.sectionGroup()
	}

	override fun getAttributeDescriptors(): Array<AttributesDescriptor> = emptyArray()

	override fun getColorDescriptors(): Array<ColorDescriptor> = arrayOf(
		ColorDescriptor(TaskBrowserBundle.message("options.colors.descriptor.contentBackground"), TaskBrowserTheme.WTB_PANE_CONTENT_BACKGROUND_COLOR, ColorDescriptor.Kind.BACKGROUND),
		ColorDescriptor(TaskBrowserBundle.message("options.colors.descriptor.contentForeground"), TaskBrowserTheme.WTB_PANE_CONTENT_FOREGROUND_COLOR, ColorDescriptor.Kind.FOREGROUND)
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
