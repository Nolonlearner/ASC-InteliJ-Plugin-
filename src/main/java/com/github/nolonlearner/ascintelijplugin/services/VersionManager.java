package com.github.nolonlearner.ascintelijplugin.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList; // 导入 ArrayList 类


public class VersionManager {
    private final HashMap<String, LinkedList<VersionRecord>> fileVersionHistory;

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

    // 生成版本ID和时间戳
    private String generateVersionId() {
        return "v" + System.currentTimeMillis();
    }

    private String getCurrentTimestamp() {
        return String.valueOf(System.currentTimeMillis());
    }

    // 保存当前版本
    public void saveVersion(String filePath, List<Change> changes, List<String> currentLines) {
        String versionId = generateVersionId();  // 生成唯一的版本 ID
        String timestamp = getCurrentTimestamp();  // 获取当前时间戳

        // 创建新的版本记录，包含变更和当前文件内容
        VersionRecord newVersion = new VersionRecord(versionId, timestamp, changes, currentLines);
        addVersion(filePath, newVersion);  // 将新版本记录添加到历史记录中

        // 写入版本文件，避免覆盖原文件
        String versionFilePath = filePath + ".v" + versionId;
        try {
            Files.write(Paths.get(versionFilePath), currentLines, StandardCharsets.UTF_8);
            System.out.println("版本已保存: " + versionFilePath);
        } catch (IOException e) {
            System.err.println("保存版本失败: " + e.getMessage());
            e.printStackTrace();
        }
    }



}
