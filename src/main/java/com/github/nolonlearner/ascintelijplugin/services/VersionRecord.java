package com.github.nolonlearner.ascintelijplugin.services;
// src/main/java/com/github/nolonlearner/ascintelijplugin/services/VersionRecord.java
import java.util.List;

public class VersionRecord {
    private String versionId;  // 版本 ID
    private String timestamp;  // 时间戳
    private List<Change> changes;  // 变更内容列表

    public VersionRecord(String versionId, String timestamp, List<Change> changes) {
        this.versionId = versionId;
        this.timestamp = timestamp;
        this.changes = changes;
    }

    // Getter 方法
    public String getVersionId() {
        return versionId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public List<Change> getChanges() {
        return changes;
    }
}
