package org.github.olloginov.ideataskbrowser

import com.intellij.notification.Notification
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.notification.NotificationsConfiguration

const val NOTIFICATION_GROUP: String = "Task Browser"

interface TaskBrowserNotifier {
	fun error(title: String, text: String)

	fun info(title: String, text: String)
}

class TaskBrowserNotifierImpl : TaskBrowserNotifier {
	init {
		NotificationsConfiguration.getNotificationsConfiguration().register(NOTIFICATION_GROUP, NotificationDisplayType.BALLOON, true)
	}

	private fun notify(title: String, message: String, messageType: NotificationType) {
		Notifications.Bus.notify(Notification(NOTIFICATION_GROUP, title, message, messageType))
	}

	override fun error(title: String, text: String) {
		notify(title, text, NotificationType.ERROR)
	}

	override fun info(title: String, text: String) {
		notify(title, text, NotificationType.INFORMATION)
	}
}
