package org.github.olloginov.ideataskbrowser.model;

import com.intellij.util.EventDispatcher;

public class TaskSearchList {
    private EventDispatcher<TaskSearchEventListener> dispatcher = EventDispatcher.create(TaskSearchEventListener.class);

    public EventDispatcher<TaskSearchEventListener> getDispatcher() {
        return dispatcher;
    }
}
