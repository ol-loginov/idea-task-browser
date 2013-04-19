package org.github.olloginov.ideataskbrowser.util;

import com.intellij.tasks.Task;

import java.util.Date;

public abstract class TaskHelper {
    public static Date getChangeDate(Task task) {
        return max(task.getCreated(), task.getUpdated());
    }

    public static Date max(Date a, Date b) {
        if (a == null || b == null) {
            return a == null ? b : a;
        }
        return a.after(b) ? a : b;
    }
}
