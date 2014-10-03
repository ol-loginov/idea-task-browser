package org.github.olloginov.ideataskbrowser.view;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.tasks.Comment;
import com.intellij.tasks.Task;
import org.github.olloginov.ideataskbrowser.TaskBrowserService;
import org.github.olloginov.ideataskbrowser.actions.OpenInBrowserAction;
import org.github.olloginov.ideataskbrowser.actions.OpenInContextAction;
import org.github.olloginov.ideataskbrowser.actions.RefreshListAction;
import org.github.olloginov.ideataskbrowser.config.TaskBrowserConfig;
import org.github.olloginov.ideataskbrowser.util.TaskHelper;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;

public class TaskBrowserPanel {
    private static final String TOOL_WINDOW_ID = "TaskBrowser";

    private JPanel root;
    private JSplitPane contentSplitter;
    private JTree tree;
    private JPanel preview;
    private JScrollPane treeScroll;
    private JEditorPane previewHtml;

    private HTMLEditorKit htmlKit = new HTMLEditorKit();

    public TaskBrowserPanel(final TaskBrowserService browserService) {
        previewHtml.setEditorKit(htmlKit);

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
                    listenTreeSingleClick(browserService, e);
                } else {
                    listenTreeDoubleClick(browserService, e);
                }
            }
        });
    }

    private void initToolbarActions() {
        final DefaultActionGroup toolbarGroup = new DefaultActionGroup();
        toolbarGroup.add(new RefreshListAction());
        toolbarGroup.add(new OpenInContextAction());
        toolbarGroup.add(new OpenInBrowserAction());

        final ActionManager actionManager = ActionManager.getInstance();
        final ActionToolbar toolbar = actionManager.createActionToolbar(TOOL_WINDOW_ID, toolbarGroup, false);
        root.add(toolbar.getComponent(), BorderLayout.WEST);
    }

    public SimpleToolWindowPanel wrapInToolWindowPanel() {
        SimpleToolWindowPanel panel = new SimpleToolWindowPanel(false);
        panel.add($$$getRootComponent$$$(), BorderLayout.CENTER);
        return panel;
    }

    public void setTreeModel(TaskTreeModel treeModel) {
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

    private void listenTreeSingleClick(TaskBrowserService browserService, MouseEvent e) {
        Task task = browserService.getSelectedTask();
        if (task == null) {
            return;
        }

        DateFormat df = DateFormat.getDateTimeInstance();
        String dateString = "";
        if (TaskHelper.getChangeDate(task) != null) {
            dateString = df.format(TaskHelper.getChangeDate(task));
        }

        StringBuilder html = new StringBuilder()
                .append(String.format("<small>%s</small><br>", dateString))
                .append(String.format("<b>%s</b><br><br>", htmlSafe(task.getPresentableName())))
                .append(String.format("<p>%s</p><br><br><br>", task.getDescription() == null ? "" : htmlBreak(task.getDescription())));

        for (Comment comment : task.getComments()) {
            html.append(String.format("<small>%s</small> %s<br>%s<br>", df.format(comment.getDate()), htmlSafe(comment.getAuthor()), htmlBreak(comment.getText())));
        }
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        root = new JPanel();
        root.setLayout(new BorderLayout(0, 0));
        contentSplitter = new JSplitPane();
        contentSplitter.setDividerLocation(332);
        root.add(contentSplitter, BorderLayout.CENTER);
        preview = new JPanel();
        preview.setLayout(new BorderLayout(0, 0));
        preview.setBackground(UIManager.getColor("EditorPane.background"));
        contentSplitter.setRightComponent(preview);
        preview.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null));
        previewHtml = new JEditorPane();
        previewHtml.setContentType("text/html");
        previewHtml.setEditable(false);
        preview.add(previewHtml, BorderLayout.CENTER);
        treeScroll = new JScrollPane();
        contentSplitter.setLeftComponent(treeScroll);
        tree = new JTree();
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        treeScroll.setViewportView(tree);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }
}
