package com.github.nolonlearner.ascintelijplugin.services;
// src/main/java/com/github/nolonlearner/ascintelijplugin/services/Change.java
public class Change {
    private String changeType; // 变更类型 ("ADD", "DELETE", "MODIFY")
    private String content;    // 变更的具体内容

    public Change(String changeType, String content) {
        this.changeType = changeType;
        this.content = content;
    }

    // Getter 方法
    public String getChangeType() {
        return changeType;
    }

    public String getContent() {
        return content;
    }
}
