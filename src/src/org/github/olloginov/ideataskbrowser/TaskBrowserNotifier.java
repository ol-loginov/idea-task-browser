package org.github.olloginov.ideataskbrowser;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.notification.NotificationsConfiguration;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;

public class TaskBrowserNotifier implements ProjectComponent {
    private static final String NOTIFICATION_GROUP = "Task Browser";
    private final Project project;

    public TaskBrowserNotifier(Project project) {
        this.project = project;
    }

    @Override
    public void initComponent() {
        NotificationsConfiguration.getNotificationsConfiguration().register(NOTIFICATION_GROUP, NotificationDisplayType.BALLOON, true);
    }

    private void notify(String title, String message, NotificationType messageType) {
        Notifications.Bus.notify(new Notification(NOTIFICATION_GROUP, title, message, messageType), project);
    }

    public void error(String title, String text) {
        notify(title, text, NotificationType.ERROR);
    }

    public void info(String title, String text) {
        notify(title, text, NotificationType.INFORMATION);
    }
}
