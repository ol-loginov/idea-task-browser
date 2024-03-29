package org.github.olloginov.ideataskbrowser.config;

import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.github.olloginov.ideataskbrowser.TaskBrowser;
import org.github.olloginov.ideataskbrowser.TaskBrowserBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

public class TaskBrowserConfigEditor implements SearchableConfigurable {
    private JRadioButton dblDoNothing;
    private JRadioButton dblDoContext;
    private JRadioButton dblDoBrowser;
    private JPanel panel;

    private final Project project;

    public TaskBrowserConfigEditor(Project project) {
        this.project = project;
    }

    @NotNull
    @Override
    public String getId() {
        return TaskBrowserConfigEditor.class.getTypeName();
    }

    @Nullable
    @Override
    public Runnable enableSearch(String option) {
        return null;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return TaskBrowserBundle.message("settings.sectionName");
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return panel;
    }

    @Override
    public boolean isModified() {
        TaskBrowserConfig serviceConfig = getServiceConfig();
        TaskBrowserConfig formConfig = getFormConfig();
        return formConfig.getDoubleClickAction() != serviceConfig.getDoubleClickAction();
    }

    @Override
    public void apply() {
        TaskBrowserConfig serviceConfig = getServiceConfig();
        serviceConfig.setDoubleClickAction(getFormConfig().getDoubleClickAction());
        setServiceConfig(serviceConfig);
    }

    @Override
    public void reset() {
        TaskBrowserConfig serviceConfig = getServiceConfig();
        switch (serviceConfig.getDoubleClickAction()) {
            case OPEN_IN_BROWSER:
                dblDoBrowser.setSelected(true);
                break;
            case SWITCH_CONTEXT:
                dblDoContext.setSelected(true);
                break;
            default:
                dblDoNothing.setSelected(true);
                break;
        }
    }

    private TaskBrowserConfig getServiceConfig() {
        TaskBrowser taskBrowser = project.getService(TaskBrowser.class);
        return taskBrowser == null ? new TaskBrowserConfig() : taskBrowser.getState();
    }

    private void setServiceConfig(TaskBrowserConfig serviceConfig) {
        TaskBrowser taskBrowser = project.getService(TaskBrowser.class);
        if (taskBrowser != null) {
            taskBrowser.loadState(serviceConfig);
        }
    }

    private TaskBrowserConfig getFormConfig() {
        TaskBrowserConfig config = new TaskBrowserConfig();
        if (dblDoNothing.isSelected()) config.setDoubleClickAction(TaskBrowserConfig.DoubleClickAction.NOTHING);
        if (dblDoBrowser.isSelected()) config.setDoubleClickAction(TaskBrowserConfig.DoubleClickAction.OPEN_IN_BROWSER);
        if (dblDoContext.isSelected()) config.setDoubleClickAction(TaskBrowserConfig.DoubleClickAction.SWITCH_CONTEXT);
        return config;
    }

    @Override
    public void disposeUIResources() {
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
        panel = new JPanel();
        panel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder(ResourceBundle.getBundle("messages/TaskBrowserBundle").getString("settings.view.dblclk")));
        dblDoNothing = new JRadioButton();
        this.$$$loadButtonText$$$(dblDoNothing, ResourceBundle.getBundle("messages/TaskBrowserBundle").getString("settings.view.dblclk.nothing"));
        panel1.add(dblDoNothing, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        dblDoContext = new JRadioButton();
        this.$$$loadButtonText$$$(dblDoContext, ResourceBundle.getBundle("messages/TaskBrowserBundle").getString("settings.view.dblclk.switchContext"));
        panel1.add(dblDoContext, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dblDoBrowser = new JRadioButton();
        this.$$$loadButtonText$$$(dblDoBrowser, ResourceBundle.getBundle("messages/TaskBrowserBundle").getString("settings.view.dblclk.browse"));
        panel1.add(dblDoBrowser, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel.add(spacer2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(dblDoNothing);
        buttonGroup.add(dblDoContext);
        buttonGroup.add(dblDoBrowser);
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadButtonText$$$(AbstractButton component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    @SuppressWarnings("unused")
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}
