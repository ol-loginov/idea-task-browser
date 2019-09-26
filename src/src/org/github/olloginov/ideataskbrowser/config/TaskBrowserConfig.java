package org.github.olloginov.ideataskbrowser.config;

import com.intellij.util.xmlb.annotations.Property;
import com.intellij.util.xmlb.annotations.XCollection;
import org.github.olloginov.ideataskbrowser.model.TaskSearch;

import java.util.ArrayList;
import java.util.List;

public class TaskBrowserConfig {
    @Property
    @XCollection(elementName = "search")
    public List<TaskSearch> searches = new ArrayList<TaskSearch>();
    @Property
    @XCollection(elementName = "filters")
    public List<String> filters= new ArrayList<String>();

    @Property
    public DoubleClickAction doubleClickAction = DoubleClickAction.NOTHING;

    public static enum DoubleClickAction {
        NOTHING,
        SWITCH_CONTEXT,
        OPEN_IN_BROWSER
    }
}
