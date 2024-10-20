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

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

public class VersionControlToolWindowFactory implements ToolWindowFactory {

    private final VersionManager versionManager;
    private final RollbackManager rollbackManager;

    public VersionControlToolWindowFactory() {
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
            String filePath = currentFile.getPath();
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
            String filePath = currentFile.getPath();
            rollbackManager.rollbackToLatest(project, filePath);
            // 可以在这里添加日志以确认回滚成功
            VersionRecord latestVersion = versionManager.getLatestVersion(filePath);
            if (latestVersion != null) {
                System.out.println("已回滚到版本 ID: " + latestVersion.getVersionId());
            }
            JOptionPane.showMessageDialog(null, "已回滚到最新版本", "回滚成功", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "未找到当前编辑的文件。", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void saveCurrentVersion(Project project) {
        VirtualFile currentFile = getCurrentFile(project);

        if (currentFile != null) {
            String filePath = currentFile.getPath();

            // 获取当前文件的所有行，作为当前版本的内容
            List<String> currentLines = new ArrayList<>();
            try {
                currentLines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 获取最新的版本记录和它的完整内容
            VersionRecord latestVersion = versionManager.getLatestVersion(filePath);
            List<String> previousLines = new ArrayList<>();
            if (latestVersion != null) {
                previousLines = latestVersion.getLines();  // 获取上一版本的完整内容
            }

            // 比较当前内容和上一版本内容，生成变更记录
            List<Change> changes = getChangesBetweenVersions(previousLines, currentLines);

            // 如果有变更，则保存新的版本
           // if (!changes.isEmpty()) {
                versionManager.saveVersion(filePath, changes, currentLines);  // 保存变更记录和完整内容
                JOptionPane.showMessageDialog(null, "当前版本已保存", "保存成功", JOptionPane.INFORMATION_MESSAGE);
           /*
           } else {
                JOptionPane.showMessageDialog(null, "无任何变更，不保存版本。", "保存提示", JOptionPane.INFORMATION_MESSAGE);
           }
          */
        } else {
            JOptionPane.showMessageDialog(null, "未找到当前编辑的文件。", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 对比两个版本的内容，生成变更记录
    private List<Change> getChangesBetweenVersions(List<String> previousLines, List<String> currentLines) {
        List<Change> changes = new ArrayList<>();
        int previousSize = previousLines.size();
        int currentSize = currentLines.size();
        int maxSize = Math.max(previousSize, currentSize);

        for (int i = 0; i < maxSize; i++) {
            if (i < previousSize && i < currentSize) {
                String previousLine = previousLines.get(i);
                String currentLine = currentLines.get(i);

                if (!previousLine.equals(currentLine)) {
                    changes.add(new Change("MODIFY", currentLine));  // 如果行内容不同，标记为修改
                }
            } else if (i >= previousSize) {
                changes.add(new Change("ADD", currentLines.get(i)));  // 新增的行
            } else {
                changes.add(new Change("DELETE", previousLines.get(i)));  // 删除的行
            }
        }

        return changes;
    }


    // 生成文件内容的哈希值
    private String generateFileHash(List<String> lines) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            for (String line : lines) {
                digest.update(line.getBytes(StandardCharsets.UTF_8));
            }
            byte[] hash = digest.digest();
            return Base64.getEncoder().encodeToString(hash);  // 将哈希值编码为字符串
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("哈希算法不存在", e);
        }
    }





    private VirtualFile getCurrentFile(Project project) {
        return FileEditorManager.getInstance(project).getSelectedEditors().length > 0
                ? FileEditorManager.getInstance(project).getSelectedEditors()[0].getFile()
                : null;
    }

}
