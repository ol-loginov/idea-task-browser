package org.github.olloginov.ideataskbrowser;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.tasks.TaskManager;
import com.intellij.tasks.TaskRepository;
import com.intellij.tasks.TaskState;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.github.olloginov.ideataskbrowser.config.TaskBrowserConfig;
import org.github.olloginov.ideataskbrowser.model.TaskSearch;
import org.github.olloginov.ideataskbrowser.model.TaskSearchList;
import org.github.olloginov.ideataskbrowser.tasks.FetchNewIssuesTask;
import org.github.olloginov.ideataskbrowser.tasks.UpdateRepositoriesTask;
import org.github.olloginov.ideataskbrowser.view.TaskTreeModel;
import org.github.olloginov.ideataskbrowser.view.TaskTreeModelWithFilter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.TreeModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.joining;

@State(name = "TaskBrowser", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
public class TaskBrowserImpl implements TaskBrowser, TaskBrowserServiceState {
    private final Project project;

    private final TaskSearchList searchList = new TaskSearchList();

    private final List<TaskState> searchFilters = new ArrayList<>();
    private TaskBrowserConfig.DoubleClickAction doubleClickAction = TaskBrowserConfig.DoubleClickAction.NOTHING;

    private final TaskTreeModel taskTreeModel;
    private final TaskTreeModelWithFilter taskTreeModelWithFilter;

    public TaskBrowserImpl(Project project) {
        this.project = project;
        this.taskTreeModel = new TaskTreeModel(searchList);
        this.taskTreeModelWithFilter = new TaskTreeModelWithFilter(taskTreeModel, getEnabledFilters());
    }

    @Override
    public TreeModel getFilteredModel() {
        return taskTreeModelWithFilter;
    }

    @Override
    public TaskBrowserConfig getState() {
        TaskBrowserConfig config = new TaskBrowserConfig();
        config.doubleClickAction = doubleClickAction;
        for (int index = 0; index < searchList.getSize(); ++index) {
            config.searches.add(searchList.getElementAt(index));
        }
        for (TaskState filter : getEnabledFilters()) {
            config.filters.add(filter == null ? "" : filter.name());
        }
        return config;
    }

    @Override
    public void loadState(@NotNull TaskBrowserConfig state) {
        TaskManager taskManager = TaskManager.getManager(project);
        TaskRepository[] repositories = taskManager.getAllRepositories();

        TaskBrowserConfig config = new TaskBrowserConfig();
        XmlSerializerUtil.copyBean(state, config);

        // remove obsolete searches
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

        if (isDiffer(searchList, config.searches)) {
            searchList.clear();
            for (TaskSearch search : config.searches) {
                searchList.add(search);
            }
            searchList.updateIcons(taskManager);
        }

        doubleClickAction = config.doubleClickAction;

        for (String filter : config.filters) {
            if (filter.isEmpty()) {
                setFilterEnabled(null, true);
            } else {
                try {
                    setFilterEnabled(TaskState.valueOf(filter), true);
                } catch (IllegalArgumentException e) {
                    // just ignore wrong value
                }
            }
        }
    }

    private static boolean isDiffer(@NotNull TaskSearchList a, @NotNull List<TaskSearch> b) {
        Function<TaskSearch, String> stringer = v -> String.format("%s:%s", v.getRepository(), v.getQuery());
        Function<List<TaskSearch>, String> listStringer = list -> list.stream().map(stringer).collect(joining("\n"));
        return !listStringer.apply(a.getInnerList()).equals(listStringer.apply(b));
    }

    @Override
    public void refresh() {
        ApplicationManager.getApplication().runReadAction(() -> ProgressManager.getInstance().run(new UpdateRepositoriesTask(project, searchList, taskTreeModel)));
    }

    @Override
    public void reloadChanges() {
        ApplicationManager.getApplication().runReadAction(() -> {
            taskTreeModelWithFilter.setStateFilter(getEnabledFilters());
            ProgressManager.getInstance().run(new FetchNewIssuesTask(project, taskTreeModel));
        });
    }

    public List<TaskState> getEnabledFilters() {
        return Collections.unmodifiableList(searchFilters);
    }

    @Override
    public boolean isFilterEnabled(TaskState target) {
        return searchFilters.contains(target);
    }

    @Override
    public void setFilterEnabled(TaskState target, boolean state) {
        boolean enabled = isFilterEnabled(target);
        if (enabled == state) {
            return;
        }

        if (state) {
            searchFilters.add(target);
        } else {
            searchFilters.remove(target);
        }

        SwingUtilities.invokeLater(() -> taskTreeModelWithFilter.setStateFilter(getEnabledFilters()));
    }
}
