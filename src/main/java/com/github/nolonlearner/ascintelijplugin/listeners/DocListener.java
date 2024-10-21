package com.github.nolonlearner.ascintelijplugin.listeners;

import com.github.difflib.DiffUtils;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.github.difflib.patch.Patch;


public class DocListener implements DocumentListener {

    private Document document; // 文档实例
    private String oldText; // 旧文本
    private String newText; // 新文本
    private ScheduledExecutorService executorService; // 定时任务执行器
    private final long DELAY = 8000; // 8000 毫秒的延迟
    private Patch<String> patch;// 保存文本差异的 Patch 对象
    private boolean isProcessing; // 标志当前是否正在处理 diff
    private final ReentrantLock lock; // 线程安全锁

    public DocListener() {
        this.document = null;
        this.oldText = "";
        this.newText = "";
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        patch = new Patch<>();
        isProcessing = false;
        lock = new ReentrantLock();
    }

    public DocListener(Document document) {
        System.out.println("DocListener 构造函数");
        System.out.println("document: " + document);
        this.document = document;
        this.oldText = document.getText(); // 初始化时获取文档的文本内容
        this.newText = document.getText(); // 新文本内容和旧文本内容相同
        this.executorService = Executors.newSingleThreadScheduledExecutor();// 创建定时任务执行器
        this.patch = new Patch<>(); // 初始化 Patch 对象
        this.isProcessing = false; // 初始化为 false
        this.lock = new ReentrantLock(); // 创建线程安全锁
    }

    @Override
    public void beforeDocumentChange(@NotNull DocumentEvent event) {
        // 如果正在处理，不更新 oldText，避免 500ms 内频繁更新
        if (isProcessing) {
            // System.out.println("正在处理，不更新 oldText");
            return;
        }
        System.out.println("获取文档" + document + "的旧文本内容");
        oldText = document.getText(); // 获取文档的旧文本内容
    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        // 如果当前有任务在执行，不再创建新的任务，防止频繁触发
        if (isProcessing) {
            return;
        }
        // 取消之前的定时任务并启动新的缓冲任务
        executorService.shutdownNow(); // 取消之前的定时任务
        executorService = Executors.newSingleThreadScheduledExecutor(); // 创建新执行器
        isProcessing = true;// 标记正在处理
        // 延迟 500ms 更新缓冲区中的文本，并生成增量 diff
        executorService.schedule(() -> {
            newText = document.getText(); // 获取最终的文本内容
            System.out.println("Document changed from: " + oldText + " to: " + newText);

            // 计算文本差异
            List<String> oldLines = Arrays.asList(oldText.split("\n"));
            List<String> newLines = Arrays.asList(newText.split("\n"));
            patch = DiffUtils.diff(oldLines, newLines); // 计算差异

            // 输出差异信息
            System.out.println("差异信息：: " + patch);
            for (var delta : patch.getDeltas()) {
                System.out.println(delta); // 输出每一个差异
            }

            // 更新旧文本为新文本
            oldText = newText;
            isProcessing = false;
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

    public Patch getPatch() {
        return patch;
    }
}