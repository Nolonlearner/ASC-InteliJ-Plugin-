package com.github.nolonlearner.ascintelijplugin.services.AutoSave;
// src/main/java/com/github/nolonlearner/ascintelijplugin/services/AutoSave/SaveDocumentCommand.java

import com.intellij.openapi.editor.Document;

/*
    * 保存文档命令
    * 实现了 SaveCommand 接口，具体执行文档的保存逻辑，保存当前文档。
    * 属性：
        FileSave autoSaveStrategy: 自动保存策略。
    * 构造函数：
        SaveDocumentCommand(FileSave autoSaveStrategy): 传入自动保存策略。
    * 方法：
        void execute(Document document): 保存当前文档。
 */
public class SaveDocumentCommand implements SaveCommand {
    private final FileSave fileSave;// 自动保存策略

    public SaveDocumentCommand(FileSave fileSave) {
        this.fileSave = fileSave;
    }

    @Override
    public void execute(Document document) {
        // 执行保存操作
        fileSave.save(document);
    }
}
