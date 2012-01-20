package org.github.olloginov.ideataskbrowser;

import com.intellij.tasks.TaskManager;
import com.intellij.tasks.TaskRepository;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.github.olloginov.ideataskbrowser.config.TaskBrowserConfig;
import org.github.olloginov.ideataskbrowser.model.TaskSearch;

import java.util.Iterator;

public class TaskBrowserServicePersister {
    private final TaskBrowserService service;

    public TaskBrowserServicePersister(TaskBrowserService service) {
        this.service = service;
    }

    public TaskBrowserConfig save() {
        TaskBrowserConfig config = new TaskBrowserConfig();
        for (int index = 0; index < service.searchList.getSize(); ++index) {
            config.searches.add(service.searchList.getElementAt(index));
        }
        return config;
    }

    public void load(TaskBrowserConfig state) {
        TaskManager taskManager = TaskManager.getManager(service.getProject());
        TaskRepository[] repositories = taskManager.getAllRepositories();

        TaskBrowserConfig config = new TaskBrowserConfig();
        if (state != null) {
            XmlSerializerUtil.copyBean(state, config);
        }
        Iterator<TaskSearch> iterator = config.searches.iterator();
        removeObsoleteSearches:
        while (iterator.hasNext()) {
            TaskSearch search = iterator.next();
            for (TaskRepository repository : repositories) {
                if (search.getRepository().equals(repository.getPresentableName())) {
                    continue removeObsoleteSearches;
                }
            }
            iterator.remove();
        }

        for (TaskSearch search : config.searches) {
            service.searchList.add(search);
        }
        service.searchList.updateIcons(taskManager);
    }
}
