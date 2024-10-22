package com.github.nolonlearner.ascintelijplugin.services;


import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
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
                lastFullContentVersion = version;
            }
            if (targetVersion != null && lastFullContentVersion != null) {
                break;
            }
        }

        if (targetVersion != null && lastFullContentVersion != null) {
            int startIndex = versions.indexOf(lastFullContentVersion);
            int targetIndex = versions.indexOf(targetVersion);

            for (int i = startIndex + 1; i <= targetIndex; i++) {
                VersionRecord currentVersion = versions.get(i);
                applyChangesToFile(filePath, currentVersion.getChanges());
            }

            // 将目标版本的完整内容写入文件
            writeFileContent(filePath, targetVersion.getLines());

            // 刷新IDE中的文件
            VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath);
            if (virtualFile != null) {
                FileEditorManager.getInstance(project).openFile(virtualFile, true);
            }

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


}
