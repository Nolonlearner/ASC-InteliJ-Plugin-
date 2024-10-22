package com.github.nolonlearner.ascintelijplugin.toolWindow;

import com.github.nolonlearner.ascintelijplugin.services.Change;
import com.github.nolonlearner.ascintelijplugin.services.RollbackManager;
import com.github.nolonlearner.ascintelijplugin.services.VersionManager;
import com.github.nolonlearner.ascintelijplugin.services.VersionRecord;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.project.Project;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import com.intellij.psi.*;
import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import com.intellij.openapi.editor.Document; // 导入 Document 类
import com.intellij.openapi.fileEditor.FileDocumentManager; // 导入 FileDocumentManager 类

import java.util.logging.Level; // 导入日志记录所需的类
import java.util.logging.Logger; // 导入 Logger 类
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.editor.Document;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

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

        // 加载历史记录
        VirtualFile currentFile = getCurrentFile(project);
        if (currentFile != null) {
            String filePath = currentFile.getPath();
            // 使用后台任务来添加历史记录，避免UI线程阻塞
            new Task.Backgroundable(project, "加载历史记录") {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    versionManager.addVersion_history(project,filePath);
                }
            }.queue();
        }

        JButton showHistoryButton = new JButton("查看版本历史");
        showHistoryButton.addActionListener(e -> {
            // 在后台线程中执行查看历史记录的操作
            new Task.Backgroundable(project, "查看版本历史") {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    showVersionHistory(project);
                }
            }.queue();
        });

        JButton rollbackButton = new JButton("回滚到最新版本");
        rollbackButton.addActionListener(e -> {
            // 在后台线程中执行回滚操作
            new Task.Backgroundable(project, "回滚到最新版本") {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    rollbackProjectToSpecificVersion(project, true);
                }
            }.queue();
        });

        JButton rollbackToSpecificButton = new JButton("回滚到特定版本");
        rollbackToSpecificButton.addActionListener(e -> {
            // 在后台线程中执行回滚操作
            new Task.Backgroundable(project, "回滚到特定版本") {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    rollbackProjectToSpecificVersion(project);
                }
            }.queue();
        });

        JButton saveVersionButton = new JButton("保存当前版本");
        saveVersionButton.addActionListener(e -> {
            // 在后台线程中执行保存操作
            new Task.Backgroundable(project, "保存当前版本") {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    saveProjectVersion(project);
                }
            }.queue();
        });

        JButton cleanHistoryButton = new JButton("清空历史版本");
        cleanHistoryButton.addActionListener(e -> {
            // 在后台线程中执行清空历史操作
            new Task.Backgroundable(project, "清空历史版本") {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    cleanProjectHistory(project);
                }
            }.queue();
        });

        panel.add(rollbackToSpecificButton);
        panel.add(showHistoryButton);
        panel.add(rollbackButton);
        panel.add(saveVersionButton);
        panel.add(cleanHistoryButton);

        toolWindow.getContentManager().addContent(ContentFactory.getInstance().createContent(panel, "", false));
    }


    // 获取项目中的所有文件并排除.history、.save和不相关的文件类型
    private List<VirtualFile> getAllProjectFiles(Project project) {
        List<VirtualFile> allFiles = new ArrayList<>();
        ProjectFileIndex fileIndex = ProjectFileIndex.getInstance(project);

        // 只保留与代码相关的文件类型
        List<String> allowedExtensions = Arrays.asList("java", "kt", "xml", "properties", "js", "ts", "html", "css");

        fileIndex.iterateContent(virtualFile -> {
            String fileName = virtualFile.getName();
            String extension = virtualFile.getExtension();
            String filePath = virtualFile.getPath();

            // 排除.history、.save文件、.iml文件以及.idea目录中的文件
            if (!fileName.endsWith(".history") && !fileName.endsWith(".save")
                    && !filePath.contains("/.idea/") && !fileName.endsWith(".iml")
                    && extension != null && allowedExtensions.contains(extension)) {
                allFiles.add(virtualFile);
            }
            return true; // 继续遍历
        });

        return allFiles;
    }



    // 保存整个项目的当前版本
    public void saveProjectVersion(Project project) {
        String versionId = versionManager.generateVersionId();
        List<VirtualFile> allFiles = getAllProjectFiles(project);

        for (VirtualFile file : allFiles) {
            if (file != null && !file.isDirectory()) { // 检查文件是否是目录
                try {
                    // 在读取操作中获取 Document 对象
                    ApplicationManager.getApplication().runReadAction(() -> {
                        Document document = FileDocumentManager.getInstance().getDocument(file);
                        if (document != null) {
                            // 在写操作中保存版本
                            ApplicationManager.getApplication().invokeLater(() -> {
                                try {
                                    saveCurrentVersion(project,file, versionId);
                                } catch (Exception e) {
                                    System.out.println("保存文件版本时发生错误 (文件类型: " + file.getFileType().getName() + "): " + file.getPath());
                                    e.printStackTrace();
                                }
                            });
                        } else {
                            // 打印详细日志，显示无法获取 Document 的文件类型和路径
                            System.out.println("无法获取文件的 Document 对象 (文件类型: " + file.getFileType().getName() + "): " + file.getPath());
                        }
                    });
                } catch (Exception e) {
                    System.out.println("保存文件版本时发生错误 (文件类型: " + file.getFileType().getName() + "): " + file.getPath());
                    e.printStackTrace();
                }
            } else {
                System.out.println("忽略目录: " + (file != null ? file.getPath() : "null"));
            }
        }

        // 在UI线程上显示保存成功信息
        ApplicationManager.getApplication().invokeLater(() -> {
            JOptionPane.showMessageDialog(null, "当前项目的所有文件版本已保存。", "保存成功", JOptionPane.INFORMATION_MESSAGE);
        });
    }




    //回滚项目到特定版本接口
    public void rollbackProjectToSpecificVersion(Project project) {
        rollbackProjectToSpecificVersion(project, false);
    }
    // 回滚项目到特定版本或最新版本实现
    public void rollbackProjectToSpecificVersion(Project project, boolean ToLatest) {
        List<VirtualFile> allFiles = getAllProjectFiles(project);
        if (ToLatest) {
            for (VirtualFile file : allFiles) {
                if (file != null) {
                    try {
                        String filePath = file.getPath();
                        LinkedList<VersionRecord> versions = versionManager.getVersions(filePath);
                        if (versions != null) {
                            if (!versions.isEmpty()) {
                                rollbackToSpecificVersion(file, project, true, "");
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error processing file: " + file.getPath());
                        e.printStackTrace();  // 输出异常堆栈
                    }
                }
            }
            JOptionPane.showMessageDialog(null, "项目中的所有文件已回滚到最新版本。", "回滚成功", JOptionPane.INFORMATION_MESSAGE);
        } else {
            String versionId = JOptionPane.showInputDialog("输入要回滚到的版本 ID:");
            for (VirtualFile file : allFiles) {
                if (file != null) {
                    try {
                        String filePath = file.getPath();
                        LinkedList<VersionRecord> versions = versionManager.getVersions(filePath);
                        if (versions != null) {
                            if (!versions.isEmpty()) {
                                rollbackToSpecificVersion(file, project, false, versionId);
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error processing file: " + file.getPath());
                        e.printStackTrace();  // 输出异常堆栈
                    }
                }
            }
            JOptionPane.showMessageDialog(null, "已回滚到版本 ID: " + versionId, "回滚成功", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    // 清空整个项目的历史版本
    public void cleanProjectHistory(Project project) {
        List<VirtualFile> allFiles = getAllProjectFiles(project);
        for (VirtualFile file : allFiles) {
            cleanHistoryVersion(project,file);
        }
        JOptionPane.showMessageDialog(null, "项目的所有历史记录已清空。", "清空成功", JOptionPane.INFORMATION_MESSAGE);
        versionManager.resetVersionId();
    }

    private void cleanHistoryVersion(Project project,VirtualFile currentFile) {
        if (currentFile != null) {
            String filePath = currentFile.getPath();
            versionManager.clearVersionHistory(project,filePath);
            //JOptionPane.showMessageDialog(null, "已清空历史版本", "成功", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "未找到文件。", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rollbackToSpecificVersion(VirtualFile currentFile, Project project, boolean ToLatest, String versionId) throws IOException {
        if (currentFile != null) {
            String filePath = currentFile.getPath();
            LinkedList<VersionRecord> versions = versionManager.getVersions(filePath);

            if (versions == null || versions.isEmpty()) {
                JOptionPane.showMessageDialog(null, "没有可用的历史记录。", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (ToLatest) {
                VersionRecord latestVersion;
                try {
                    latestVersion = versions.getLast();
                } catch (NoSuchElementException e) {
                    JOptionPane.showMessageDialog(null, "没有找到最新版本。", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                versionId = latestVersion.getVersionId();
            }

            for (VersionRecord version : versions) {
                if (version.getVersionId().equals(versionId)) {
                    VersionRecord previousVersion = findNearestFullContentVersion(versions, version);

                    // 获取回滚前的文件内容
                    String originalContent = new String(currentFile.contentsToByteArray(), StandardCharsets.UTF_8);

                    if (previousVersion != null) {
                        applyChangesToFile(filePath, previousVersion, version);
                    } else {
                        rollbackManager.rollbackToVersion(project, filePath, versionId);
                    }

                    // 获取回滚后的文件内容
                    VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath);
                    String newContent = new String(virtualFile.contentsToByteArray(), StandardCharsets.UTF_8);

                    // 如果文件内容发生了变化，才刷新和重新打开
                    if (!originalContent.equals(newContent)) {
                        virtualFile.refresh(false, false);
                        FileDocumentManager.getInstance().reloadFromDisk((Document) virtualFile);
                        FileEditorManager.getInstance(project).openFile(virtualFile, true);
                    }

                    return;
                }
            }
            JOptionPane.showMessageDialog(null, "未找到版本 ID: " + versionId, "错误", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "未找到文件。", "错误", JOptionPane.ERROR_MESSAGE);
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
        try {
            // 获取当前正在编辑的文件
            VirtualFile currentFile = getCurrentFile(project);

            if (currentFile != null) {
                String filePath = currentFile.getPath();
                // 读取版本历史记录
                HashMap<String, List<VersionRecord>> fileHistories = readVersionHistory(project);

                // 检查当前文件是否存在历史记录
                List<VersionRecord> versions = fileHistories.get(filePath);
                if (versions == null || versions.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "没有找到历史记录。", "版本历史", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                // 构建版本历史的显示内容
                StringBuilder history = new StringBuilder();
                for (VersionRecord version : versions) {
                    history.append("版本 ID: ").append(version.getVersionId())
                            .append(", 时间戳: ").append(version.getTimestamp())
                            .append("\n");
                }

                // 显示历史记录
                JOptionPane.showMessageDialog(null, history.toString(), "版本历史", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "未找到当前编辑的文件。", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            // 捕获并显示异常信息
            JOptionPane.showMessageDialog(null, "显示历史记录时发生错误: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // 读取 JSON 格式的历史记录
    private HashMap<String, List<VersionRecord>> readVersionHistory(Project project) {
        Gson gson = new Gson();
        String projectPath = project.getBasePath(); // 从 Project 对象获取项目路径
        try (FileReader reader = new FileReader(projectPath + "/history.txt")) {
            // 读取为 HashMap<String, List<VersionRecord>>
            Type fileHistoriesType = new TypeToken<HashMap<String, List<VersionRecord>>>() {}.getType();
            return gson.fromJson(reader, fileHistoriesType);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    private void saveCurrentVersion(Project project,VirtualFile currentFile,String versionId) {

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
                versionManager.saveVersion(project,filePath, currentLines,versionId);

              //  JOptionPane.showMessageDialog(null, "当前版本已保存", "保存成功", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "未找到当前文档。", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "未找到当前编辑的文件。", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private VirtualFile getCurrentFile(Project project) {
        return FileEditorManager.getInstance(project).getSelectedEditors().length > 0
                ? FileEditorManager.getInstance(project).getSelectedEditors()[0].getFile()
                : null;
    }

}
