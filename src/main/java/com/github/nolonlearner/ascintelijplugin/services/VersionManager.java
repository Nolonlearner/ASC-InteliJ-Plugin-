package com.github.nolonlearner.ascintelijplugin.services;
// src/main/java/com/github/nolonlearner/ascintelijplugin/services/VersionManager.java
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class VersionManager {
    private HashMap<String, LinkedList<VersionRecord>> fileVersionHistory;

    public VersionManager() {
        fileVersionHistory = new HashMap<>();
    }

    // 添加版本记录
    public void addVersion(String filePath, VersionRecord versionRecord) {
        fileVersionHistory
                .computeIfAbsent(filePath, k -> new LinkedList<>()) // 如果没有历史记录，则创建新链表
                .add(versionRecord); // 添加新版本记录
    }

    // 获取指定文件的所有版本记录
    public LinkedList<VersionRecord> getVersions(String filePath) {
        return fileVersionHistory.getOrDefault(filePath, new LinkedList<>());
    }

    // 获取最新版本
    public VersionRecord getLatestVersion(String filePath) {
        LinkedList<VersionRecord> versions = getVersions(filePath);
        return versions.isEmpty() ? null : versions.getLast();
    }

    // 生成版本ID和时间戳
    private String generateVersionId() {
        return "v" + System.currentTimeMillis();
    }

    private String getCurrentTimestamp() {
        return String.valueOf(System.currentTimeMillis());
    }

    // 保存当前版本
    public void saveVersion(String filePath, List<Change> changes) {
        String versionId = generateVersionId(); // 生成版本 ID
        String timestamp = getCurrentTimestamp(); // 获取时间戳
        VersionRecord newVersion = new VersionRecord(versionId, timestamp, changes);
        addVersion(filePath, newVersion);
    }
}
