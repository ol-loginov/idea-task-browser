package org.github.olloginov.ideataskbrowser

import com.intellij.tasks.TaskState

interface TaskBrowserServiceState {
    fun isFilterEnabled(target: TaskState?): Boolean

    fun setFilterEnabled(target: TaskState?, state: Boolean)
}
