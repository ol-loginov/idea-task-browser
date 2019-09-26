package org.github.olloginov.ideataskbrowser.view;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.tasks.Comment;
import com.intellij.tasks.Task;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.treeStructure.Tree;
import org.github.olloginov.ideataskbrowser.TaskBrowserService;
import org.github.olloginov.ideataskbrowser.actions.OpenInBrowserAction;
import org.github.olloginov.ideataskbrowser.actions.OpenInContextAction;
import org.github.olloginov.ideataskbrowser.actions.RefreshListAction;
import org.github.olloginov.ideataskbrowser.actions.SetIssueFilterAction;
import org.github.olloginov.ideataskbrowser.config.TaskBrowserConfig;
import org.github.olloginov.ideataskbrowser.util.TaskHelper;

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

public class TaskBrowserPanel {
    public static final String TOOL_WINDOW_ID = "TaskBrowser";

    private JPanel root;
    private Tree tree;
    private JEditorPane previewHtml;

    public TaskBrowserPanel(final TaskBrowserService browserService) {
        initComponent();

        previewHtml.setEditorKit(new HTMLEditorKit());

        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setCellRenderer(new TaskTreeRenderer());

        setTreeModel(new TaskTreeModel());
        initToolbarActions(browserService);

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() < 1) {
                    return;
                }
                if (e.getClickCount() == 1) {
                    listenTreeSingleClick(browserService);
                } else {
                    listenTreeDoubleClick(browserService, e);
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

    private void initToolbarActions(TaskBrowserService browserService) {
        final DefaultActionGroup toolbarGroup = new DefaultActionGroup();
        toolbarGroup.add(new RefreshListAction());
        toolbarGroup.add(new OpenInContextAction());
        toolbarGroup.add(new OpenInBrowserAction());
        toolbarGroup.add(new SetIssueFilterAction(browserService));

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

    private void listenTreeSingleClick(TaskBrowserService browserService) {
        Task task = browserService.getSelectedTask();
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

    private void listenTreeDoubleClick(final TaskBrowserService browserService, MouseEvent e) {
        TaskBrowserConfig config = browserService.getState();
        if (config == null) return;

        AnAction action;
        switch (config.doubleClickAction) {
            case SWITCH_CONTEXT:
                action = new OpenInContextAction();
                break;
            case OPEN_IN_BROWSER:
                action = new OpenInBrowserAction();
                break;
            default:
                action = null;
        }

        if (action != null) {
            ActionManager.getInstance().tryToExecute(action, e, tree, null, true);
        }
    }
}
