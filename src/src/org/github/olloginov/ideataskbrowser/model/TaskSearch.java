package org.github.olloginov.ideataskbrowser.model;

import com.intellij.util.xmlb.annotations.Tag;
import com.intellij.util.xmlb.annotations.Transient;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Tag("search")
public class TaskSearch {
    private String uid = UUID.randomUUID().toString();
    private String repository;
    private String query;
    private Icon icon;

    private AtomicBoolean updating = new AtomicBoolean(false);

    @Tag("uid")
    @NotNull
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid == null ? this.uid : uid;
    }

    @Tag("repository")
    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    @Tag("query")
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Transient
    public AtomicBoolean getUpdating() {
        return updating;
    }

    @Transient
    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }
}
