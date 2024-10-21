package com.github.nolonlearner.ascintelijplugin.listeners;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DocListener implements DocumentListener {

    private Document document; // 文档实例
    private String oldText; // 旧文本
    private String newText; // 新文本
    private ScheduledExecutorService executorService; // 定时任务执行器
    private final long DELAY = 300; // 300 毫秒的延迟

    public DocListener() {
        this.document = null;
        this.oldText = "";
        this.newText = "";
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public DocListener(Document document) {
        System.out.println("DocListener 构造函数");
        System.out.println("document: " + document);
        this.document = document;
        this.oldText = document.getText(); // 初始化时获取文档的文本内容
        this.newText = document.getText(); // 新文本内容和旧文本内容相同
        this.executorService = Executors.newSingleThreadScheduledExecutor();// 创建定时任务执行器
    }

    @Override
    public void beforeDocumentChange(@NotNull DocumentEvent event) {
        System.out.println("获取文档" + document + "的旧文本内容");
        oldText = document.getText(); // 获取文档的旧文本内容
    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        executorService.shutdownNow(); // 取消之前的定时任务
        executorService = Executors.newSingleThreadScheduledExecutor(); // 创建新执行器

        // 延迟获取新文本内容
        executorService.schedule(() -> {
            newText = document.getText(); // 获取最终的文本内容
            System.out.println("Document changed from: " + oldText + " to: " + newText);
            System.out.println(document.getLineCount()); // 获取文档的行数
            // 这里可以调用其他处理方法，例如保存 local history
            // VersionManager.saveVersion(document);
        }, DELAY, TimeUnit.MILLISECONDS);
    }

    // 清理资源的方法
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}