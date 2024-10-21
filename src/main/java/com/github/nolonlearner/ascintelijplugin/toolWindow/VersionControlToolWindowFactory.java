package com.github.nolonlearner.ascintelijplugin.toolWindow;

import com.github.nolonlearner.ascintelijplugin.services.Change;
import com.github.nolonlearner.ascintelijplugin.services.RollbackManager;
import com.github.nolonlearner.ascintelijplugin.services.VersionManager;
import com.github.nolonlearner.ascintelijplugin.services.VersionRecord;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.vfs.LocalFileSystem;
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

import com.intellij.openapi.editor.Document; // 导入 Document 类
import com.intellij.openapi.fileEditor.FileDocumentManager; // 导入 FileDocumentManager 类
import java.util.Arrays; // 导入 Arrays 类
import java.util.logging.Level; // 导入日志记录所需的类
import java.util.logging.Logger; // 导入 Logger 类

public class VersionControlToolWindowFactory implements ToolWindowFactory {

    private final VersionManager versionManager;
    private final RollbackManager rollbackManager;
    private static final Logger logger = Logger.getLogger(RollbackManager.class.getName()); // 创建日志记录器

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

        JButton rollbackToSpecificButton = new JButton("回滚到特定版本");
        rollbackToSpecificButton.addActionListener(e -> rollbackToSpecificVersion(project));

        JButton saveVersionButton = new JButton("保存当前版本");
        saveVersionButton.addActionListener(e -> saveCurrentVersion(project));

        panel.add(rollbackToSpecificButton);
        panel.add(showHistoryButton);
        panel.add(rollbackButton);
        panel.add(saveVersionButton);

        toolWindow.getContentManager().addContent(ContentFactory.getInstance().createContent(panel, "", false));
    }

    private void rollbackToSpecificVersion(Project project) {
        VirtualFile currentFile = getCurrentFile(project);
        if (currentFile != null) {
            String filePath = currentFile.getPath();
            LinkedList<VersionRecord> versions = versionManager.getVersions(filePath);

            String versionId = JOptionPane.showInputDialog("输入要回滚到的版本 ID:");
            for (VersionRecord version : versions) {
                if (version.getVersionId().equals(versionId)) {
                    VersionRecord previousVersion = findNearestFullContentVersion(versions, version);
                    if (previousVersion != null) {
                        // 这里调用需要是两个 VersionRecord 参数的 applyChangesToFile
                        applyChangesToFile(filePath, previousVersion, version); // 确保此行的参数类型匹配
                        JOptionPane.showMessageDialog(null, "已回滚到版本 ID: " + versionId, "回滚成功", JOptionPane.INFORMATION_MESSAGE);

                        // 刷新编辑器中的文件内容
                        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath);
                        if (virtualFile != null) {
                            FileEditorManager.getInstance(project).openFile(virtualFile, true);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "未找到完整内容版本，无法回滚。", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                    return;
                }
            }
            JOptionPane.showMessageDialog(null, "未找到版本 ID: " + versionId, "错误", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "未找到当前编辑的文件。", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }


    private VersionRecord findNearestFullContentVersion(LinkedList<VersionRecord> versions, VersionRecord targetVersion) {
        // 从目标版本开始向前查找最近的完整内容版本
        for (int i = versions.indexOf(targetVersion) - 1; i >= 0; i--) {
            if (versions.get(i).isFullContent()) {
                return versions.get(i);
            }
        }
        return null;  // 如果未找到
    }

    private void applyChangesToFile(String filePath, VersionRecord fromVersion, VersionRecord toVersion) {
        // 按照变更记录逐个版本推进
        List<Change> changes = new ArrayList<>();
        LinkedList<VersionRecord> versions = versionManager.getVersions(filePath);
        int startIndex = versions.indexOf(fromVersion);
        int endIndex = versions.indexOf(toVersion);

        for (int i = startIndex + 1; i <= endIndex; i++) {
            changes.addAll(versions.get(i).getChanges());
        }

        // 将最终的文件内容写入当前文件
        try {
            List<String> currentLines = new ArrayList<>(fromVersion.getLines()); // 初始化当前内容
            for (Change change : changes) {
                int lineIndex; // 将 lineIndex 的声明移到这里，作用域更广
                switch (change.getChangeType()) {
                    case "ADD":
                        currentLines.add(change.getContent());
                        break;
                    case "MODIFY":
                        lineIndex = change.getLineNumber();
                        if (lineIndex >= 0 && lineIndex < currentLines.size()) {
                            currentLines.set(lineIndex, change.getContent());
                        }
                        break;
                    case "DELETE":
                        lineIndex = change.getLineNumber();
                        if (lineIndex >= 0 && lineIndex < currentLines.size()) {
                            currentLines.remove(lineIndex);
                        }
                        break;
                }
            }
            Files.write(Paths.get(filePath), currentLines, StandardCharsets.UTF_8);
            System.out.println("文件已成功回滚到版本 ID: " + toVersion.getVersionId());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "文件写入失败: " + e.getMessage(), e); // 使用日志记录异常
        }
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
            // 获取当前编辑器的文档
            Document document = FileDocumentManager.getInstance().getDocument(currentFile);
            if (document != null) {
                String filePath = currentFile.getPath();
                // 获取当前文件的所有行
                String currentContent = document.getText();

                // 将内容按行分割成列表
                List<String> currentLines = Arrays.asList(currentContent.split("\n"));

                // 调用 VersionManager 的 saveVersion 方法
                versionManager.saveVersion(filePath, currentLines);

                JOptionPane.showMessageDialog(null, "当前版本已保存", "保存成功", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "未找到当前文档。", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "未找到当前编辑的文件。", "错误", JOptionPane.ERROR_MESSAGE);
        }
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
