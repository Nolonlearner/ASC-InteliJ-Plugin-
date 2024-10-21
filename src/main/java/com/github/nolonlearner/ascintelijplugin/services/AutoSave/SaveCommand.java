package com.github.nolonlearner.ascintelijplugin.services.AutoSave;
// src/main/java/com/github/nolonlearner/ascintelijplugin/services/AutoSave/SaveCommand.java
import com.intellij.openapi.editor.Document;
/*
    * 保存命令接口
    * 定义了一个 execute 方法，用于执行保存操作。
    * 参数：
        Document document: 当前文档对象。
 */
public interface SaveCommand {
    void execute(Document document);// 执行保存操作
}
