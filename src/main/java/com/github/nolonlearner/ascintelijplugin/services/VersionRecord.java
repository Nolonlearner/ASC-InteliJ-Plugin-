package com.github.nolonlearner.ascintelijplugin.services;

import java.util.ArrayList;

import java.util.List;

public class VersionRecord {
    private final String versionId;
    private final String timestamp;
    private final List<Change> changes;  // 保存此版本相对于上一版本的变更
    private final List<String> lines;    // 保存此版本的文件行内容（若保存完整内容）
    private final boolean isFullContent; // 是否保存了完整文件内容

    // 构造函数
    public VersionRecord(String versionId, String timestamp, List<Change> changes, List<String> lines, boolean isFullContent) {
        this.versionId = versionId;
        this.timestamp = timestamp;
        this.changes = changes != null ? changes : new ArrayList<>();  // 若无变更则初始化空列表
        this.lines = isFullContent ? lines : new ArrayList<>();  // 若保存完整内容则保存行内容，否则为空
        this.isFullContent = isFullContent;
    }

    // 获取版本ID
    public String getVersionId() {
        return versionId;
    }

    // 获取时间戳
    public String getTimestamp() {
        return timestamp;
    }

    // 获取变更记录
    public List<Change> getChanges() {
        return changes;
    }

    // 获取保存的文件行内容（适用于完整内容保存的版本）
    public List<String> getLines() {
        return lines;
    }

    // 判断该版本是否保存了完整内容
    public boolean isFullContent() {
        return isFullContent;
    }

}
