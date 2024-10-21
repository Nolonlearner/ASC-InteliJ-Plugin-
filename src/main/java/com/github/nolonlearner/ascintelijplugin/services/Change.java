package com.github.nolonlearner.ascintelijplugin.services;
// src/main/java/com/github/nolonlearner/ascintelijplugin/services/Change.java
public class Change {
    private String changeType; // 变更类型 ("ADD", "DELETE", "MODIFY")
    private String content;    // 变更的具体内容
    private int lineNumber;    // 变更所在的行号

    public Change(String changeType, String content, int lineNumber) {
        this.changeType = changeType;
        this.content = content;
        this.lineNumber = lineNumber; // 行号
    }

    // Getter 方法
    public String getChangeType() {
        return changeType;
    }

    public String getContent() {
        return content;
    }

    public int getLineNumber() {
        return lineNumber; // 获取行号
    }

    // 用于生成变更的简洁表示
    public String toLineRepresentation() {
        return changeType + " line " + lineNumber + ": " + content;
    }

    // 用于反序列化的静态方法 (从文本生成 Change 对象)
    public static Change fromLineRepresentation(String line) {
        // 假设输入格式为 "CHANGE_TYPE line LINE_NUMBER: CONTENT"
        String[] parts = line.split(":", 2);
        String[] metaData = parts[0].split(" ");
        String changeType = metaData[0];
        int lineNumber = Integer.parseInt(metaData[2]); // 获取行号
        String content = parts[1].trim();
        return new Change(changeType, content, lineNumber);
    }
}
