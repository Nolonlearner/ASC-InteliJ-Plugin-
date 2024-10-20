package com.github.nolonlearner.ascintelijplugin.listeners;
// src/main/java/com/github/nolonlearner/ascintelijplugin/listeners/DocListener.java
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import org.jetbrains.annotations.NotNull;

public class DocListener implements DocumentListener {

    Document document;// 文档实例
    private String oldText;// 旧文本
    private String newText;// 新文本

    public DocListener () {
        this.document = null;
        this.oldText = "";
        this.newText = "";
    }

    public DocListener(Document document) {
        System.out.println("DocListener 构造函数");
        System.out.println("document: " + document);
        this.document = document;
        this.oldText = document.getText();// 初始化的时候获取文档的文本内容
        this.newText = document.getText();// 新文本内容和旧文本内容相同
    }

    /*
     * 文档内容改变之前触发的事件
     */
    @Override
    public void beforeDocumentChange(@NotNull DocumentEvent event) {
        System.out.println("获取文档"+ document +"的旧文本内容");
        oldText = document.getText();// 获取文档的旧文本内容
    }

    /*
     * 文档内容改变之后触发的事件
     */
    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        System.out.println("获取文档"+ document +"的新文本内容");
        newText = document.getText();// 获取文档的新文本内容
        System.out.println("Document changed from: " + oldText + " to: " + newText);
        // 这里可以调用其他处理方法，例如保存local history
        // VersionManager.saveVersion(document);
    }
}
