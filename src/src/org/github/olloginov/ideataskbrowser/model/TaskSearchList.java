package org.github.olloginov.ideataskbrowser.model;

import com.intellij.tasks.TaskManager;
import com.intellij.tasks.TaskRepository;
import com.intellij.util.EventDispatcher;
import com.intellij.util.containers.Convertor;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TaskSearchList implements ListModel {
    private List<TaskSearch> list = new ArrayList<>();

    private EventDispatcher<ListDataListener> dataDispatcher = EventDispatcher.create(ListDataListener.class);

    public void clear() {
        int lastSize = getSize();
        if (lastSize > 0) {
            list.clear();
            dataDispatcher.getMulticaster().intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, 0, lastSize - 1));
        }
    }

    @Override
    public int getSize() {
        return list.size();
    }

    @Override
    public TaskSearch getElementAt(int index) {
        return list.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        dataDispatcher.addListener(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        dataDispatcher.removeListener(l);
    }

    public void add(TaskSearch task) {
        int index = getSize();
        list.add(task);
        dataDispatcher.getMulticaster().intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index));
    }

    public TaskSearch findSearchByRepository(String name) {
        for (int index = 0; index < getSize(); ++index) {
            TaskSearch search = getElementAt(index);
            if (name.equals(search.getRepository())) {
                return search;
            }
        }
        return null;
    }

    private void updateAll(Convertor<TaskSearch, TaskSearch> updater) {
        for (int index = list.size() - 1; index >= 0; --index) {
            TaskSearch search = updater.convert(list.get(index));
            if (search == null) {
                removeAt(index);
            } else {
                updateAt(index, search);
            }
        }
    }

    private void updateAt(int index, TaskSearch search) {
        list.set(index, search);
        dataDispatcher.getMulticaster().contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index, index));
    }

    private void removeAt(int index) {
        list.remove(index);
        dataDispatcher.getMulticaster().intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index));
    }

    public void updateIcons(TaskManager taskManager) {
        final Map<String, TaskRepository> repositoryMap = new TreeMap<>();
        for (TaskRepository r : taskManager.getAllRepositories()) {
            repositoryMap.put(r.getPresentableName(), r);
        }

        updateAll(new Convertor<TaskSearch, TaskSearch>() {
            @Override
            public TaskSearch convert(TaskSearch o) {
                TaskRepository repository = repositoryMap.get(o.getRepository());
                if (repository == null) {
                    return null;
                }
                o.setIcon(repository.getRepositoryType().getIcon());
                return o;
            }
        });
    }
}
