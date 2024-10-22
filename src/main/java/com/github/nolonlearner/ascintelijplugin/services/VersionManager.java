package com.github.nolonlearner.ascintelijplugin.services;
import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.DiffException;
import com.github.difflib.patch.Patch;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.project.Project;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


public class VersionManager {
    private final HashMap<String, LinkedList<VersionRecord>> fileVersionHistory;
    private static int currentVersionId = 1; // 从1开始递增

    public VersionManager() {
        fileVersionHistory = new HashMap<>();
    }


    // 保存历史记录到文件系统，使用 JSON 格式，写入前先清空文件
    // 保存所有文件的历史记录到项目目录下的单个文件 "history.txt"
    public void saveVersionHistory(Project project) {
        String projectPath = project.getBasePath(); // 从 Project 对象获取项目路径

        if (projectPath == null) {
            System.out.println("无法获取项目路径");
            return;
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create(); // 使用 pretty-print 格式化输出
        String historyFilePath = projectPath + "/history.txt";

        try (FileWriter writer = new FileWriter(historyFilePath, false)) { // 每次保存时清空文件
            // 将文件历史记录转换为 JSON
            String jsonContent = gson.toJson(fileVersionHistory);
            writer.write(jsonContent);
            System.out.println("所有文件的历史记录已保存为 JSON 格式: " + historyFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 清空历史记录
    public void clearVersionHistory(Project project,String filePath) {
        String projectPath = project.getBasePath(); // 从 Project 对象获取项目路径
        File historyFile = new File(projectPath + "/history.txt");
        if (historyFile.exists()) {
            try (FileOutputStream fos = new FileOutputStream(historyFile)) {
                // 写入空内容，清空文件
                fos.write(new byte[0]);
                System.out.println("历史记录已清空");
            } catch (IOException e) {
                System.err.println("无法清空历史记录: " + e.getMessage());
            }
        } else {
            System.out.println("历史记录文件不存在");
        }

        // 清空内存中的记录fileVersionHistory
        fileVersionHistory.clear();
        System.out.println("fileVersionHistory 已清空");
    }



    // 从文件系统中加载历史记录
    public void addVersion_history(Project project,String filePath) {
        String projectPath = project.getBasePath(); // 从 Project 对象获取项目路径
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(projectPath + "/history.txt"))) {
            List<VersionRecord> versionHistory = (List<VersionRecord>) ois.readObject();
            if (versionHistory != null) {
                for (VersionRecord version : versionHistory) {
                    addVersion(filePath, version);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 添加版本记录
    public void addVersion(String filePath, VersionRecord versionRecord) {
        fileVersionHistory
                .computeIfAbsent(filePath, k -> new LinkedList<>())
                .addLast(versionRecord); // 确保添加到链表末尾
    }

    // 获取指定文件的所有版本记录
    public LinkedList<VersionRecord> getVersions(String filePath) {
        return fileVersionHistory.getOrDefault(filePath, new LinkedList<>());
    }

    // 获取最新版本
    public VersionRecord getLatestVersion(String filePath) {
        LinkedList<VersionRecord> versions = getVersions(filePath);
        return versions.isEmpty() ? null : versions.getLast(); // 获取最后一个版本
    }

    // 生成递增的版本ID，格式化为8位数字
    public String generateVersionId() {
        String versionId = String.format("%08d", currentVersionId);
        currentVersionId++; // 每次生成ID后递增
        return versionId;
    }

    public void resetVersionId() {
        currentVersionId = 1;
    }

    private String getCurrentTimestamp() {
        return String.valueOf(System.currentTimeMillis());
    }

    // 保存当前版本
    public void saveVersion(Project project,String filePath, List<String> currentLines,String versionId) {
        String timestamp = getCurrentTimestamp();  // 获取当前时间戳

        // 获取当前版本数量
        LinkedList<VersionRecord> versions = getVersions(filePath);
        int versionCount = versions.size();

        // 如果是第一次保存，直接保存完整内容
        if (versionCount == 0) {
            VersionRecord newVersion = new VersionRecord(versionId, timestamp, new ArrayList<>(), currentLines, true);
            addVersion(filePath, newVersion);
            saveFullContent(filePath, versionId, currentLines);  // 保存完整文件
            System.out.println("初始化版本已保存 (完整内容): " + versionId);
            return;
        }

        // 获取上一版本的 versionId
        VersionRecord previousVersion = versions.get(versionCount - 1);
        String previousVersionId = previousVersion.getVersionId();

        // 根据上一版本的 versionId 构建文件路径，读取完整内容
        List<String> previousLines = readFullContentFromFile(filePath, previousVersionId);

        // 使用 diff 比较当前文件和上一版本的文件，生成变更记录
        List<Change> changes = getChangesFromDiff(previousLines, currentLines);

        // 根据版本数量决定是否保存完整内容
        boolean isFullContent = (versionCount % 5 == 0);

        // 创建新的版本记录，包含变更和当前文件内容
        VersionRecord newVersion = new VersionRecord(versionId, timestamp, changes, currentLines, isFullContent);
        addVersion(filePath, newVersion);  // 将新版本记录添加到历史记录中

        // 保存完整内容
        saveFullContent(filePath, versionId, currentLines);
        // 保存变更记录
        saveChangeRecords(filePath, versionId, changes);
        // 更新文件系统中的历史记录存档

        saveVersionHistory(project);

        // 删除上一版本的存档文件，根据当前版本的类型决定删除内容还是变更记录
        if(versionCount>1){
            if (previousVersion.isFullContent()) {
                deleteFile(filePath + ".changes.v" + previousVersion.getVersionId()+".save");
            } else {
                deleteFile(filePath + ".content.v" + previousVersion.getVersionId()+".save");
            }
        }
    }

    // 读取文件系统中的完整内容
    private List<String> readFullContentFromFile(String filePath, String versionId) {
        String fullContentFilePath = filePath + ".content.v" + versionId+".save";  // 构建文件路径
        try {
            return Files.readAllLines(Paths.get(fullContentFilePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();  // 返回空列表以防止进一步错误
        }
    }

    // 使用 DiffUtils 计算变更记录并获取行号
    private List<Change> getChangesFromDiff(List<String> oldLines, List<String> newLines) {
        List<Change> changes = new ArrayList<>();
        Patch<String> patch = DiffUtils.diff(oldLines, newLines);

        for (AbstractDelta<String> delta : patch.getDeltas()) {
            DeltaType type = delta.getType();
            List<String> sourceLines = delta.getSource().getLines();  // 旧版本的行
            List<String> targetLines = delta.getTarget().getLines();  // 新版本的行
            int sourcePosition = delta.getSource().getPosition();  // 旧版本行的起始位置
            int targetPosition = delta.getTarget().getPosition();  // 新版本行的起始位置

            switch (type) {
                case INSERT:
                    for (String line : targetLines) {
                        changes.add(new Change("ADD", line, targetPosition)); // 使用新行位置
                        targetPosition++;
                    }
                    break;
                case DELETE:
                    for (String line : sourceLines) {
                        changes.add(new Change("DELETE", line, sourcePosition));
                        sourcePosition++;
                    }
                    break;
                case CHANGE:
                    // 处理修改情况，先删除旧行再添加新行
                    for (String line : sourceLines) {
                        changes.add(new Change("DELETE", line, sourcePosition));
                        sourcePosition++;
                    }
                    for (String line : targetLines) {
                        changes.add(new Change("MODIFY", line, targetPosition)); // 使用目标位置
                        targetPosition++;
                    }
                    break;
            }
        }

        return changes;
    }


    // 保存完整文件内容
    private void saveFullContent(String filePath, String versionId, List<String> lines) {
        String contentFilePath = filePath + ".content.v" + versionId+".save";
        try {
            Files.write(Paths.get(contentFilePath), lines, StandardCharsets.UTF_8);
            System.out.println("完整文件内容已保存: " + contentFilePath);
        } catch (IOException e) {
            System.err.println("保存完整文件内容失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 保存变更记录
    private void saveChangeRecords(String filePath, String versionId, List<Change> changes) {
        String changesFilePath = filePath + ".changes.v" + versionId+".save";
        try {
            List<String> changesAsStrings = changes.stream()
                    .map(change -> change.getChangeType() + " (Line " + change.getLineNumber() + "): " + change.getContent())
                    .collect(Collectors.toList());
            Files.write(Paths.get(changesFilePath), changesAsStrings, StandardCharsets.UTF_8);
            System.out.println("变更记录已保存: " + changesFilePath);
        } catch (IOException e) {
            System.err.println("保存变更记录失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 删除文件
    private void deleteFile(String filePath) {
        try {
            Files.deleteIfExists(Paths.get(filePath));
            System.out.println("删除了文件: " + filePath);
        } catch (IOException e) {
            System.err.println("删除文件失败: " + e.getMessage());
        }
    }

}
