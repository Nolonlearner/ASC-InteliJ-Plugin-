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

    public void rollbackToLatest(String filePath) {
        VersionRecord latestVersion = versionManager.getLatestVersion(filePath);
        if (latestVersion != null) {
            try {
                // 将最新版本的完整内容写入当前文件
                Files.write(Paths.get(filePath), latestVersion.getLines(), StandardCharsets.UTF_8);
                System.out.println("已成功回滚到最新版本: " + filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
