package org.github.olloginov.ideataskbrowser.config

import com.intellij.application.options.colors.*
import com.intellij.openapi.editor.colors.ColorKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorAndFontDescriptorsProvider
import com.intellij.openapi.options.colors.ColorDescriptor
import org.github.olloginov.ideataskbrowser.TaskBrowserBundle

class TaskBrowserColors : ColorAndFontPanelFactory, ColorAndFontDescriptorsProvider {
	companion object {
		var TASK_TREE_BACKGROUND_COLOR = ColorKey.createColorKey("TASK_TREE_BACKGROUND_COLOR")

		fun sectionGroup(): String = TaskBrowserBundle.message("settings.sectionName")
	}

	override fun getDisplayName(): String {
		return sectionGroup()
	}

	override fun getAttributeDescriptors(): Array<AttributesDescriptor> = emptyArray()

	override fun getColorDescriptors(): Array<ColorDescriptor> {
		val descriptors = mutableListOf<ColorDescriptor>()

		descriptors.add(ColorDescriptor(TaskBrowserBundle.message("options.colors.descriptor.taskTreeBackground"), TASK_TREE_BACKGROUND_COLOR, ColorDescriptor.Kind.BACKGROUND))

		return descriptors.toTypedArray()
	}

	override fun getPanelDisplayName(): String {
		return sectionGroup()
	}

	override fun createPanel(options: ColorAndFontOptions): NewColorAndFontPanel {
		val schemesPanel = SchemesPanel(options)
		val optionsPanel = OptionsPanelImpl(options, schemesPanel, sectionGroup())
		val previewPanel = TaskBrowserColorsPreviewPanel()

		schemesPanel.addListener(object : ColorAndFontSettingsListener.Abstract() {
			override fun schemeChanged(source: Any) {
				previewPanel.setColorScheme(options.selectedScheme)
				optionsPanel.updateOptionsList()
			}
		})

		return NewColorAndFontPanel(schemesPanel, optionsPanel, previewPanel, sectionGroup(), null, null)
	}
}