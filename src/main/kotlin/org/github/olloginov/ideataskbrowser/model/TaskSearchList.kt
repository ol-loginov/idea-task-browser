package org.github.olloginov.ideataskbrowser.model

import com.intellij.tasks.TaskManager
import com.intellij.util.EventDispatcher
import javax.swing.ListModel
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener

class TaskSearchList : ListModel<TaskSearch> {
    private val list = mutableListOf<TaskSearch>()
    private val dataDispatcher = EventDispatcher.create(ListDataListener::class.java)

    fun clear() {
        val lastSize = size
        if (lastSize > 0) {
            list.clear()
            dataDispatcher.multicaster.intervalRemoved(ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, 0, lastSize - 1))
        }
    }

    fun getInnerList(): List<TaskSearch> = list.toList()

    override fun getSize() = list.size

    override fun getElementAt(index: Int): TaskSearch = list[index]

    override fun addListDataListener(l: ListDataListener) {
        dataDispatcher.addListener(l)
    }

    override fun removeListDataListener(l: ListDataListener) {
        dataDispatcher.removeListener(l)
    }

    fun add(task: TaskSearch) {
        val index = size
        list.add(task)
        dataDispatcher.multicaster.intervalAdded(ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index))
    }

    fun findSearchByRepository(name: String): TaskSearch? {
        for (index in 0 until size) {
            val search = getElementAt(index)
            if (name == search.getRepository()) {
                return search
            }
        }
        return null
    }

    private fun updateAll(updater: (TaskSearch) -> TaskSearch?) {
        for (index in list.size - 1 downTo 0) {
            val search = updater(list[index])
            if (search == null) {
                removeAt(index)
            } else {
                updateAt(index, search)
            }
        }
    }

    private fun updateAt(index: Int, search: TaskSearch) {
        list[index] = search
        dataDispatcher.multicaster.contentsChanged(ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index, index))
    }

    private fun removeAt(index: Int) {
        list.removeAt(index)
        dataDispatcher.multicaster.intervalRemoved(ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index))
    }

    fun updateIcons(taskManager: TaskManager) {
        val repositoryMap = taskManager.allRepositories.associateBy { r -> r.presentableName }

        updateAll { o ->
            val repository = repositoryMap[o.getRepository()]
            if (repository == null) {
                null
            } else {
                o.setIcon(repository.repositoryType.icon)
                o
            }
        }
    }
}
