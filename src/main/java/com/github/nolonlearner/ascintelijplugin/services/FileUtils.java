package com.github.nolonlearner.ascintelijplugin.services;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.project.Project;

public class FileUtils {
    // 刷新文件视图和编辑器状态
    public static void refreshFileView(String filePath) {
        VirtualFile file = LocalFileSystem.getInstance().findFileByPath(filePath);
        if (file != null) {
            Document document = FileDocumentManager.getInstance().getDocument(file);
            if (document != null) {
                FileDocumentManager.getInstance().reloadFromDisk(document); // 重新加载文档内容
            }
        }
    }


    public static void refreshEditor(Project project, String filePath) {
        VirtualFile projectFile = project.getProjectFile();
        if (projectFile != null) {
            VirtualFile file = projectFile.getParent().findFileByRelativePath(filePath);
            if (file != null) {
                FileEditorManager.getInstance(project).openFile(file, true); // 重新打开文件
            }
        }
    }


}
