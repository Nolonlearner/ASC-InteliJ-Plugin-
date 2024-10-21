package com.github.nolonlearner.ascintelijplugin.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList; // 导入 ArrayList 类
import java.util.stream.Collectors;


public class VersionManager {
    private final HashMap<String, LinkedList<VersionRecord>> fileVersionHistory;
    private static int currentVersionId = 1; // 从1开始递增

    public VersionManager() {
        fileVersionHistory = new HashMap<>();
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
    private String generateVersionId() {
        String versionId = String.format("%08d", currentVersionId);
        currentVersionId++; // 每次生成ID后递增
        return versionId;
    }

    private String getCurrentTimestamp() {
        return String.valueOf(System.currentTimeMillis());
    }

    // 保存当前版本
    public void saveVersion(String filePath, List<Change> changes, List<String> currentLines) {
        String versionId = generateVersionId();  // 生成唯一的版本 ID
        String timestamp = getCurrentTimestamp();  // 获取当前时间戳

        // 获取当前版本数量
        LinkedList<VersionRecord> versions = getVersions(filePath);
        int versionCount = versions.size();
        boolean isFullContent = (versionCount == 1 || versionCount % 5 == 0);  // 是否保存完整内容

        // 创建新的版本记录，包含变更和当前文件内容
        VersionRecord newVersion = new VersionRecord(versionId, timestamp, changes, currentLines, isFullContent);
        addVersion(filePath, newVersion);  // 将新版本记录添加到历史记录中

        // 保存完整内容
        String contentFilePath = filePath + ".content.v" + versionId;
        try {
            Files.write(Paths.get(contentFilePath), currentLines, StandardCharsets.UTF_8);
            System.out.println("版本已保存 (完整内容): " + contentFilePath);
        } catch (IOException e) {
            System.err.println("保存完整内容失败: " + e.getMessage());
            e.printStackTrace();
        }

        // 保存变更记录
        String changesFilePath = filePath + ".changes.v" + versionId;
        try {
            List<String> changesAsStrings = changes.stream()
                    .map(change -> change.getChangeType() + ": " + change.getContent())
                    .collect(Collectors.toList());
            Files.write(Paths.get(changesFilePath), changesAsStrings, StandardCharsets.UTF_8);
            System.out.println("变更记录已保存: " + changesFilePath);
        } catch (IOException e) {
            System.err.println("保存变更记录失败: " + e.getMessage());
            e.printStackTrace();
        }

        // 处理上一版本的存档
        if (versionCount > 0) {
            VersionRecord previousVersion = versions.get(versionCount - 1);  // 获取上一版本
            String previousVersionContentFilePath = filePath + ".content.v" + previousVersion.getVersionId();
            String previousVersionChangesFilePath = filePath + ".changes.v" + previousVersion.getVersionId();

            // 检查条件以决定保留或删除完整内容和变更记录
            if (isFullContent) {
                // 如果当前版本是完整内容，那么删除上一版本的变更记录
                deleteFile(previousVersionChangesFilePath);
            } else {
                // 如果当前版本是差异记录，删除上一版本的完整内容
                deleteFile(previousVersionContentFilePath);
            }
        }
    }

// 删除指定文件
    private void deleteFile(String filePath) {
        try {
            Files.deleteIfExists(Paths.get(filePath));
            System.out.println("删除了文件: " + filePath);
        } catch (IOException e) {
            System.err.println("删除文件失败: " + e.getMessage());
        }
    }




}
