package com.github.nolonlearner.ascintelijplugin.listeners;
// src/main/java/com/github/nolonlearner/ascintelijplugin/listeners/DocListener.java
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

public class DocListener implements DocumentListener {

    Document document;// 文档实例
    private String oldText;// 旧文本
    private String newText;// 新文本

    private Timer timer;// 定时器
    private final long DELAY = 300; // 300 毫秒的延迟

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
       /* System.out.println("获取文档"+ document +"的新文本内容");
        newText = document.getText();// 获取文档的新文本内容
        System.out.println("Document changed from: " + oldText + " to: " + newText);
        // 这里可以调用其他处理方法，例如保存local history
        // VersionManager.saveVersion(document);*/


        /*
            * 使用定时器的方式，延迟一段时间后再获取文档的新文本内容
            * 这样可以避免在文本内容改变的瞬间就获取文本内容，导致获取到的文本内容不准确
            * 例如，用户在输入文本的时候，每输入一个字符，就会触发一次文本内容改变的事件
         */
        if (timer != null) {
            timer.cancel(); // 取消之前的定时任务
        }

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                newText = document.getText(); // 获取最终的文本内容
                System.out.println("Document changed from: " + oldText + " to: " + newText);
                // 这里可以调用其他处理方法，例如保存 local history
                // VersionManager.saveVersion(document);
            }
        }, DELAY);
    }
}
