package org.github.olloginov.ideataskbrowser;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.notification.NotificationsConfiguration;

public interface TaskBrowserNotifier {
    String NOTIFICATION_GROUP = "Task Browser";

    void error(String title, String text);

    void info(String title, String text);
}

class TaskBrowserNotifierImpl implements TaskBrowserNotifier {
    public TaskBrowserNotifierImpl() {
        NotificationsConfiguration.getNotificationsConfiguration().register(NOTIFICATION_GROUP, NotificationDisplayType.BALLOON, true);
    }

    private void notify(String title, String message, NotificationType messageType) {
        Notifications.Bus.notify(new Notification(NOTIFICATION_GROUP, title, message, messageType));
    }

    public void error(String title, String text) {
        notify(title, text, NotificationType.ERROR);
    }

    public void info(String title, String text) {
        notify(title, text, NotificationType.INFORMATION);
    }
}