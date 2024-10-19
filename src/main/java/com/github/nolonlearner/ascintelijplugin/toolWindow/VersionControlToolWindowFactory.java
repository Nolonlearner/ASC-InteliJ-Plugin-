package com.github.nolonlearner.ascintelijplugin.toolWindow;

import com.github.nolonlearner.ascintelijplugin.services.Change;
import com.github.nolonlearner.ascintelijplugin.services.RollbackManager;
import com.github.nolonlearner.ascintelijplugin.services.VersionManager;
import com.github.nolonlearner.ascintelijplugin.services.VersionRecord;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.project.Project;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class VersionControlToolWindowFactory implements ToolWindowFactory {

    private final VersionManager versionManager; // 版本管理模块实例
    private final RollbackManager rollbackManager; // 回滚管理模块实例

    public VersionControlToolWindowFactory() {
        // 初始化版本管理和回滚管理
        versionManager = new VersionManager();
        rollbackManager = new RollbackManager(versionManager);
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, ToolWindow toolWindow) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JButton showHistoryButton = new JButton("查看版本历史");
        showHistoryButton.addActionListener(e -> showVersionHistory(project));

        JButton rollbackButton = new JButton("回滚到最新版本");
        rollbackButton.addActionListener(e -> rollbackToLatestVersion(project));

        JButton saveVersionButton = new JButton("保存当前版本");
        saveVersionButton.addActionListener(e -> saveCurrentVersion(project));

        panel.add(showHistoryButton);
        panel.add(rollbackButton);
        panel.add(saveVersionButton);

        toolWindow.getContentManager().addContent(ContentFactory.getInstance().createContent(panel, "", false));
    }

    private void showVersionHistory(Project project) {
        VirtualFile currentFile = getCurrentFile(project);

        if (currentFile != null) {
            String filePath = currentFile.getPath(); // 获取当前文件的路径
            LinkedList<VersionRecord> versions = versionManager.getVersions(filePath);
            StringBuilder history = new StringBuilder();

            for (VersionRecord version : versions) {
                history.append("版本 ID: ").append(version.getVersionId())
                        .append(", 时间戳: ").append(version.getTimestamp())
                        .append("\n");
            }

            JOptionPane.showMessageDialog(null, history.toString(), "版本历史", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "未找到当前编辑的文件。", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rollbackToLatestVersion(Project project) {
        VirtualFile currentFile = getCurrentFile(project);

        if (currentFile != null) {
            String filePath = currentFile.getPath(); // 获取当前文件的路径
            rollbackManager.rollbackToLatest(project, filePath); // 传递 project 参数
            JOptionPane.showMessageDialog(null, "已回滚到最新版本", "回滚成功", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "未找到当前编辑的文件。", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveCurrentVersion(Project project) {
        VirtualFile currentFile = getCurrentFile(project);

        if (currentFile != null) {
            String filePath = currentFile.getPath(); // 获取当前文件的路径
            List<Change> changes = getChangesFromCurrentFile(filePath); // 需要实现此方法
            versionManager.saveVersion(filePath, changes);
            JOptionPane.showMessageDialog(null, "当前版本已保存", "保存成功", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "未找到当前编辑的文件。", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 辅助方法，用于获取当前编辑的文件
    private VirtualFile getCurrentFile(Project project) {
        return FileEditorManager.getInstance(project).getSelectedEditors().length > 0
                ? FileEditorManager.getInstance(project).getSelectedEditors()[0].getFile()
                : null;
    }


    private List<Change> getChangesFromCurrentFile(String filePath) {
        List<Change> changes = new ArrayList<>();
        try {
            List<String> currentLines = Files.readAllLines(Paths.get(filePath));
            // 在这里可以添加逻辑来比较 currentLines 与上一个版本的内容
            // 假设你已经有上一个版本的内容，这里简化处理
            List<String> previousLines = versionManager.getLatestVersion(filePath).getChanges()
                    .stream().map(Change::getContent).toList();
            for (String line : currentLines) {
                if (!previousLines.contains(line)) {
                    changes.add(new Change("ADD", line)); // 假设新增行
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return changes;
    }
}
