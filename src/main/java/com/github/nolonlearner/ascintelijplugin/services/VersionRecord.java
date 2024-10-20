package com.github.nolonlearner.ascintelijplugin.services;
import java.util.List;

public class VersionRecord {
    private final String versionId;
    private final String timestamp;
    private final List<Change> changes;
    private final List<String> lines;  // 保存当前版本的文件内容

    public VersionRecord(String versionId, String timestamp, List<Change> changes, List<String> lines) {
        this.versionId = versionId;
        this.timestamp = timestamp;
        this.changes = changes;
        this.lines = lines;
    }

    public String getVersionId() {
        return versionId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public List<Change> getChanges() {
        return changes;
    }

    public List<String> getLines() {
        return lines;  // 返回当前版本的文件内容
    }
}

