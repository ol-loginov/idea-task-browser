package org.github.olloginov.ideataskbrowser;

import com.intellij.tasks.TaskManager;
import com.intellij.tasks.TaskRepository;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.github.olloginov.ideataskbrowser.config.TaskBrowserConfig;
import org.github.olloginov.ideataskbrowser.model.TaskSearch;

public class TaskBrowserServicePersister {
    private final TaskBrowserService service;

    public TaskBrowserServicePersister(TaskBrowserService service) {
        this.service = service;
    }

    public TaskBrowserConfig save() {
        TaskBrowserConfig config = new TaskBrowserConfig();
        config.doubleClickAction = service.doubleClickAction;
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

        // remove obsolete searches
        removeObsoleteSearches(repositories, config);

        service.searchList.clear();
        for (TaskSearch search : config.searches) {
            service.searchList.add(search);
        }
        service.searchList.updateIcons(taskManager);
        service.doubleClickAction = config.doubleClickAction;
        service.reloadChanges();
    }

    private void removeObsoleteSearches(TaskRepository[] repositories, TaskBrowserConfig config) {
        for (int i = config.searches.size() - 1; i >= 0; --i) {
            TaskSearch search = config.searches.get(i);
            for (TaskRepository repository : repositories) {
                if (search.getRepository().equals(repository.getPresentableName())) {
                    search = null;
                    break;
                }
            }
            if (search != null)
                config.searches.remove(i);
        }
    }
}
