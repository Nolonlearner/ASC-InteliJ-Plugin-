package com.github.nolonlearner.ascintelijplugin.services;


import com.intellij.openapi.project.Project;
import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class RollbackManager {
    private VersionManager versionManager;

    public RollbackManager(VersionManager versionManager) {
        this.versionManager = versionManager;
    }

    // 回滚到最新版本
    public void rollbackToLatest(Project project, String filePath) {
        VersionRecord latestVersion = versionManager.getLatestVersion(filePath);
        if (latestVersion != null) {
            applyVersion(filePath, latestVersion);  // 使用 applyVersion 来确保覆盖文件内容
        }
        // 刷新文件视图和编辑器状态
        FileUtils.refreshFileView(filePath);
        FileUtils.refreshEditor(project, filePath);  // 传递 project 参数
    }

    // 应用变更到文件中
    private void applyVersion(String filePath, VersionRecord versionRecord) {
        List<Change> changes = versionRecord.getChanges();
        StringBuilder fileContent = new StringBuilder();

        for (Change change : changes) {
            // 假设我们将所有更改按顺序应用，构建文件的新内容
            fileContent.append(change.getContent()).append("\n");
        }

        // 写入文件
        try {
            Files.write(Paths.get(filePath), fileContent.toString().getBytes());

            // Debugging: 读取并打印文件内容，确认写入成功
            String newContent = new String(Files.readAllBytes(Paths.get(filePath)));
            System.out.println("New file content: " + newContent); // 打印出新的文件内容

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 回滚到特定版本
    public void rollbackToVersion(Project project, String filePath, String versionId) {
        LinkedList<VersionRecord> versions = versionManager.getVersions(filePath);
        if (versions != null) {
            for (VersionRecord version : versions) {
                if (version.getVersionId().equals(versionId)) {
                    applyVersion(filePath, version);  // 应用特定版本
                    JOptionPane.showMessageDialog(null, "已回滚到版本 ID: " + versionId, "回滚成功", JOptionPane.INFORMATION_MESSAGE);
                    // 刷新文件视图和编辑器状态
                    FileUtils.refreshFileView(filePath);
                    FileUtils.refreshEditor(project, filePath);  // 传递 project 参数
                    break;
                }
            }
        }
    }
}
