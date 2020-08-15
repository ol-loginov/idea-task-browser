package org.github.olloginov.ideataskbrowser.view;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.tasks.Comment;
import com.intellij.tasks.Task;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.treeStructure.Tree;
import org.github.olloginov.ideataskbrowser.TaskBrowser;
import org.github.olloginov.ideataskbrowser.TaskBrowserToolWindow;
import org.github.olloginov.ideataskbrowser.actions.OpenInBrowserAction;
import org.github.olloginov.ideataskbrowser.actions.OpenInContextAction;
import org.github.olloginov.ideataskbrowser.actions.RefreshListAction;
import org.github.olloginov.ideataskbrowser.actions.SetIssueFilterAction;
import org.github.olloginov.ideataskbrowser.config.TaskBrowserConfig;
import org.github.olloginov.ideataskbrowser.util.TaskHelper;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;

public class TaskBrowserPanel implements TaskBrowserToolWindow {
    public static final String TOOL_WINDOW_ID = "TaskBrowser";

    private JPanel root;
    private Tree tree;
    private JEditorPane previewHtml;

    public TaskBrowserPanel(@NotNull Project project) {
        initComponent();

        previewHtml.setEditorKit(new HTMLEditorKit());

        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setCellRenderer(new TaskTreeRenderer());

        setTreeModel(new TaskTreeModel());
        initToolbarActions();

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() < 1) {
                    return;
                }
                if (e.getClickCount() == 1) {
                    listenTreeSingleClick();
                } else {
                    listenTreeDoubleClick(project, e);
                }
            }
        });
    }

    private void initComponent() {
        previewHtml = new JEditorPane();
        previewHtml.setBorder(BorderFactory.createEmptyBorder());
        previewHtml.setContentType("text/html");
        previewHtml.setEditable(false);

        JScrollPane scrollPane1 = ScrollPaneFactory.createScrollPane();
        scrollPane1.setBorder(null);
        scrollPane1.setBackground(UIManager.getColor("EditorPane.selectionForeground"));
        scrollPane1.setForeground(UIManager.getColor("EditorPane.background"));
        scrollPane1.setViewportView(previewHtml);

        JPanel preview = new JPanel();
        preview.setLayout(new BorderLayout(10, 10));
        preview.setBackground(UIManager.getColor("EditorPane.background"));
        preview.add(scrollPane1, BorderLayout.CENTER);

        tree = new Tree();
        tree.setBorder(null);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);

        JScrollPane treeScroll = ScrollPaneFactory.createScrollPane();
        treeScroll.setBorder(null);
        treeScroll.setViewportView(tree);

        JBSplitter contentSplitter = new JBSplitter();
        contentSplitter.setBorder(null);
        contentSplitter.setOrientation(false);
        contentSplitter.setProportion(.5f);
        contentSplitter.setFirstComponent(treeScroll);
        contentSplitter.setSecondComponent(preview);

        root = new JPanel();
        root.setBorder(null);
        root.setLayout(new BorderLayout(0, 0));
        root.add(contentSplitter, BorderLayout.CENTER);
    }

    private void initToolbarActions() {
        final DefaultActionGroup toolbarGroup = new DefaultActionGroup();
        toolbarGroup.add(new RefreshListAction());
        toolbarGroup.add(new OpenInContextAction(this));
        toolbarGroup.add(new OpenInBrowserAction(this));
        toolbarGroup.add(new SetIssueFilterAction());

        final ActionManager actionManager = ActionManager.getInstance();
        final ActionToolbar toolbar = actionManager.createActionToolbar(TOOL_WINDOW_ID, toolbarGroup, false);
        root.add(toolbar.getComponent(), BorderLayout.WEST);
    }

    public SimpleToolWindowPanel wrapInToolWindowPanel() {
        SimpleToolWindowPanel panel = new SimpleToolWindowPanel(false);
        panel.add(root, BorderLayout.CENTER);
        return panel;
    }

    public void setTreeModel(TreeModel treeModel) {
        this.tree.setModel(treeModel);
    }

    public TreeNode getSelectedNode() {
        TreePath selectionPath = tree.getSelectionPath();
        if (selectionPath == null) {
            return null;
        }
        return (TreeNode) selectionPath.getLastPathComponent();
    }

    public static String htmlBreak(String text) {
        return text == null ? "" : text.replaceAll("(\\r\\n|\\r|\\n)", "<br>");
    }

    public static String htmlSafe(String text) {
        return text == null ? "" : text
                .replaceAll("<a", "&lt;a")
                .replaceAll("<script", "&lt;script");
    }

    @Override
    public Task getSelectedTask() {
        TreeNode node = getSelectedNode();
        if (node instanceof TaskTreeNode) {
            return ((TaskTreeNode) node).getTask();
        }
        return null;
    }

    private void listenTreeSingleClick() {
        Task task = getSelectedTask();
        if (task == null) {
            return;
        }

        DateFormat df = DateFormat.getDateTimeInstance();
        String dateString = "";
        if (TaskHelper.getChangeDate(task) != null) {
            dateString = df.format(TaskHelper.getChangeDate(task));
        }

        StringBuilder html = new StringBuilder("<html><body style='padding: 5px;'>")
                .append(String.format("<small>%s</small><br>", dateString))
                .append(String.format("<b>%s</b><br><br>", htmlSafe(task.getPresentableName())))
                .append(String.format("<p>%s</p><br><br><br>", task.getDescription() == null ? "" : htmlBreak(task.getDescription())));

        for (Comment comment : task.getComments()) {
            html.append(String.format("<b>%s</b> <small>%s</small><br>%s<br>", df.format(comment.getDate()), htmlSafe(comment.getAuthor()), comment.getText()));
        }
        html.append("</body></html>");
        previewHtml.setText(html.toString());
    }

    private void listenTreeDoubleClick(@NotNull Project project, @NotNull MouseEvent e) {
        TaskBrowser taskBrowser = ServiceManager.getService(project, TaskBrowser.class);
        if (taskBrowser == null) {
            return;
        }
        TaskBrowserConfig config = taskBrowser.getState();
        if (config == null) {
            return;
        }

        switch (config.doubleClickAction) {
            case SWITCH_CONTEXT:
                ActionManager.getInstance().tryToExecute(new OpenInContextAction(this), e, tree, null, true);
                break;
            case OPEN_IN_BROWSER:
                ActionManager.getInstance().tryToExecute(new OpenInBrowserAction(this), e, tree, null, true);
                break;
        }
    }
}
