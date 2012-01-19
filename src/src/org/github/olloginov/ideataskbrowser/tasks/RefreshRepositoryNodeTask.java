package org.github.olloginov.ideataskbrowser.tasks;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.tasks.Task;
import com.intellij.tasks.TaskRepository;
import com.intellij.util.continuation.ContinuationContext;
import com.intellij.util.continuation.TaskDescriptor;
import com.intellij.util.continuation.Where;
import org.github.olloginov.ideataskbrowser.TaskBrowserBundle;
import org.github.olloginov.ideataskbrowser.TaskBrowserNotifier;
import org.github.olloginov.ideataskbrowser.exceptions.RepositoryException;
import org.github.olloginov.ideataskbrowser.model.RepositoryTreeNode;
import org.github.olloginov.ideataskbrowser.model.TaskTreeNode;
import org.github.olloginov.ideataskbrowser.model.TreeNodeRef;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

public class RefreshRepositoryNodeTask extends TaskDescriptor {
    private static final Logger logger = Logger.getInstance(RefreshRepositoryNodeTask.class);

    private final TaskRepository repository;
    private final Project project;
    private final TreeNodeRef<RepositoryTreeNode> nodeRef;

    public RefreshRepositoryNodeTask(Project project, TaskRepository repository, TreeNodeRef<RepositoryTreeNode> nodeRef) {
        super("Refresh tasks from " + repository.getPresentableName(), Where.POOLED);
        this.repository = repository;
        this.nodeRef = nodeRef;
        this.project = project;
    }

    public RepositoryTreeNode getNode() {
        return nodeRef.getNode();
    }

    @Override
    public void run(ContinuationContext context) {
        TaskBrowserNotifier notifier = ServiceManager.getService(project, TaskBrowserNotifier.class);

        final ProgressIndicator indicator = ProgressManager.getInstance().getProgressIndicator();
        if (indicator.isCanceled()) {
            return;
        }

        String title = TaskBrowserBundle.message("task.RefreshRepositoryNodeTask.title", repository.getPresentableName());
        try {
            int tasks = run(indicator);
            if (tasks > 0) {
                notifier.info(title, TaskBrowserBundle.message("task.RefreshRepositoryNodeTask.finishing.newIssues", tasks));
            } else {
                notifier.info(title, TaskBrowserBundle.message("task.RefreshRepositoryNodeTask.finishing.noIssues"));
            }
        } catch (Exception e) {
            notifier.error(title, TaskBrowserBundle.message("task.RefreshRepositoryNodeTask.fetchError"));
            if (!(e instanceof RepositoryException)) {
                logger.error(e);
            }
        }
    }

    private int run(ProgressIndicator indicator) throws RepositoryException, InvocationTargetException, InterruptedException {
        String name = repository.getPresentableName();
        indicator.setText(TaskBrowserBundle.message("task.RefreshRepositoryNodeTask.starting", name));

        Date skipFetchBefore = getNode().getLatestTaskDate();
        Date fetchDate = new Date();


        int count = 0;
        while (count < 100 && (skipFetchBefore == null || skipFetchBefore.after(fetchDate))) {
            for (final Task task : fetchChanges(fetchDate)) {
                Date taskCreated = task.getCreated();
                if (taskCreated == null) {
                    throw new RepositoryException(TaskBrowserBundle.message("error.unsupported.noCreatedDate"));
                }

                int taskNodeIndex = getNode().findTaskNode(task);
                if (taskNodeIndex >= 0) {
                    // node was found
                    skipFetchBefore = fetchDate;
                    break;
                }

                // node not found, but got place where insert
                final int insertAt = -(taskNodeIndex + 1);
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        nodeRef.insertChild(insertAt, new TaskTreeNode(task));
                    }
                });

                fetchDate = min(fetchDate, taskCreated);
                count++;
            }
        }
        return count;
    }

    private Date min(Date a, Date b) {
        if (a == null || b == null) {
            return a == null ? b : a;
        }
        return a.before(b) ? a : b;
    }

    public Task[] fetchChanges(Date date) throws RepositoryException {
        try {
            return repository.getIssues(null, 20, date.getTime() - 1);
        } catch (Exception e) {
            throw new RepositoryException(TaskBrowserBundle.message("error.connection.broken"), e);
        }
    }
}
