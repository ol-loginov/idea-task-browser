package org.github.olloginov.ideataskbrowser;

import com.intellij.notification.*;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;

public class TaskBrowserNotifier extends AbstractProjectComponent {
    private static final String NOTIFICATION_GROUP = "Task Browser";

    public TaskBrowserNotifier(Project project) {
        super(project);
    }

    @Override
    public void initComponent() {
        super.initComponent();
        NotificationsConfiguration.getNotificationsConfiguration().register(NOTIFICATION_GROUP, NotificationDisplayType.BALLOON, true);
    }

    private void notify(String title, String message, NotificationType messageType) {
        Notifications.Bus.notify(new Notification(NOTIFICATION_GROUP, title, message, messageType), myProject);
    }

    public void error(String title, String text) {
        notify(title, text, NotificationType.ERROR);
    }

    public void info(String title, String text) {
        notify(title, text, NotificationType.INFORMATION);
    }
}
