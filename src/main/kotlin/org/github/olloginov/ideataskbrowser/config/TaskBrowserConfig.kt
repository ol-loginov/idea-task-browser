package org.github.olloginov.ideataskbrowser.config

import com.intellij.util.xmlb.annotations.Property
import com.intellij.util.xmlb.annotations.XCollection
import org.github.olloginov.ideataskbrowser.model.TaskSearch

class TaskBrowserConfig {
	@Property
	@XCollection(elementName = "search")
	val searches = mutableListOf<TaskSearch>()

	@Property
	@XCollection(elementName = "filters")
	val filters = mutableListOf<String>()

	@Property
	var doubleClickAction = DoubleClickAction.NOTHING

	enum class DoubleClickAction {
		NOTHING,
		SWITCH_CONTEXT,
		OPEN_IN_BROWSER
	}
}
