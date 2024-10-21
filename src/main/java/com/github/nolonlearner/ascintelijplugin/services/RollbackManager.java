package com.github.nolonlearner.ascintelijplugin.services;


import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class RollbackManager {
    private final VersionManager versionManager;

    public RollbackManager(VersionManager versionManager) {
        this.versionManager = versionManager;
    }



    public void rollbackToVersion(Project project, String filePath, String targetVersionId) {
        LinkedList<VersionRecord> versions = versionManager.getVersions(filePath);
        VersionRecord targetVersion = null;
        VersionRecord lastFullContentVersion = null;

        // 找到目标版本及最近的完整版本
        for (VersionRecord version : versions) {
            if (version.getVersionId().equals(targetVersionId)) {
                targetVersion = version;
            }
            if (version.isFullContent()) {
                lastFullContentVersion = version;  // 更新最近的完整版本
            }
            if (targetVersion != null && lastFullContentVersion != null) {
                break;  // 找到目标版本和最近完整版本，停止循环
            }
        }

        if (targetVersion != null && lastFullContentVersion != null) {
            // 从最近的完整版本开始应用变更
            int startIndex = versions.indexOf(lastFullContentVersion);
            int targetIndex = versions.indexOf(targetVersion);

            for (int i = startIndex + 1; i <= targetIndex; i++) {
                VersionRecord currentVersion = versions.get(i);
                applyChangesToFile(filePath, currentVersion.getChanges());
            }

            // 将目标版本的完整内容写入当前文件
            writeFileContent(filePath, targetVersion.getLines());
        }
    }

    // 应用变更到文件
    private void applyChangesToFile(String filePath, List<Change> changes) {
        try {
            List<String> currentLines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
            for (Change change : changes) {
                switch (change.getChangeType()) {
                    case "ADD":
                        currentLines.add(change.getContent());
                        break;
                    case "MODIFY":
                        int lineIndex = change.getLineNumber();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 将指定内容写入文件
    private void writeFileContent(String filePath, List<String> lines) {
        try {
            Files.write(Paths.get(filePath), lines, StandardCharsets.UTF_8);
            System.out.println("已回滚到版本: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    // 回滚到最新版本
    public void rollbackToLatest(Project project, String filePath) {
        VersionRecord latestVersion = versionManager.getLatestVersion(filePath);
        if (latestVersion != null) {
            applyVersion(project, filePath, latestVersion);  // 传递 project 参数
        }
        // 刷新文件视图和编辑器状态
        FileUtils.refreshFileView(filePath);
        FileUtils.refreshEditor(project, filePath);  // 传递 project 参数
    }


    // 应用变更到文件中
    private void applyVersion(Project project, String filePath, VersionRecord versionRecord) {
        List<Change> changes = versionRecord.getChanges();
        StringBuilder fileContent = new StringBuilder();

        for (Change change : changes) {
            fileContent.append(change.getContent()).append("\n");
        }

        // 写入文件
        try {
            Files.write(Paths.get(filePath), fileContent.toString().getBytes());

            // 刷新编辑器中的文件内容
            VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath);
            if (virtualFile != null) {
                // 刷新文件视图和编辑器状态
                FileEditorManager.getInstance(project).openFile(virtualFile, true);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
