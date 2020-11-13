package org.github.olloginov.ideataskbrowser.util

import com.intellij.tasks.Task
import java.util.*

object TaskHelper {
	fun getChangeDate(task: Task): Date? {
		return max(task.created, task.updated)
	}

	private fun max(a: Date?, b: Date?): Date? {
		if (a == null || b == null) {
			return a ?: b
		}
		return if (a.after(b)) a else b
	}
}
