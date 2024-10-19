package com.github.nolonlearner.ascintelijplugin.toolWindow;

import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.project.Project;
import com.intellij.ui.content.ContentFactory;
import javax.swing.*;

public class VersionControlToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // 添加按钮
        JButton showHistoryButton = new JButton("查看版本历史");
        showHistoryButton.addActionListener(e -> showVersionHistory(project));

        JButton rollbackButton = new JButton("回滚到最新版本");
        rollbackButton.addActionListener(e -> rollbackToLatestVersion(project));

        JButton saveVersionButton = new JButton("保存当前版本");
        saveVersionButton.addActionListener(e -> saveCurrentVersion(project));

        panel.add(showHistoryButton);
        panel.add(rollbackButton);
        panel.add(saveVersionButton);

        // 使用新方法创建内容
        toolWindow.getContentManager().addContent(ContentFactory.getInstance().createContent(panel, "", false));
    }

    private void showVersionHistory(Project project) {
        // TODO: 调用 VersionManager 显示版本历史
        JOptionPane.showMessageDialog(null, "显示版本历史的功能待实现");
    }

    private void rollbackToLatestVersion(Project project) {
        // TODO: 调用 RollbackManager 回滚到最新版本
        JOptionPane.showMessageDialog(null, "回滚到最新版本的功能待实现");
    }

    private void saveCurrentVersion(Project project) {
        // TODO: 调用 VersionManager 保存当前版本
        JOptionPane.showMessageDialog(null, "保存当前版本的功能待实现");
    }
}
