package com.github.nolonlearner.ascintelijplugin.services;
import java.util.ArrayList;
import java.util.List;

public class VersionRecord {
    private final String versionId;
    private final String timestamp;
    private final List<Change> changes;
    private final List<String> lines;  // 保存当前版本的文件内容
    private final boolean isFullContent;  // 是否保存了完整文件内容

    public VersionRecord(String versionId, String timestamp, List<Change> changes, List<String> lines, boolean isFullContent) {
        this.versionId = versionId;
        this.timestamp = timestamp;
        this.changes = changes;
        this.lines = lines;
        this.isFullContent = isFullContent;
    }

    public boolean isFullContent() {
        return isFullContent;
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

    public List<String> getChangesAsLines() {
        List<String> modifiedLines = new ArrayList<>();
        for (Change change : changes) {
            switch (change.getChangeType()) {
                case "ADD":
                case "MODIFY":
                    modifiedLines.add(change.getContent());  // 将添加或修改的行加入
                    break;
                case "DELETE":
                    // 对于删除类型，可根据需要决定是否做进一步处理
                    break;
            }
        }
        return modifiedLines;
    }
}

