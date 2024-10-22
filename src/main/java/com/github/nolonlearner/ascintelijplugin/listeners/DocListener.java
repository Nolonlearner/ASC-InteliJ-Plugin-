package com.github.nolonlearner.ascintelijplugin.listeners;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Chunk;
import com.github.difflib.patch.DeltaType;
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
    private final long DELAY = 10000; //  10000 毫秒
    private Patch<String> patch; // 保存文本差异的 Patch 对象
    private boolean isProcessing; // 标志当前是否正在处理 diff
    private final ReentrantLock lock = new ReentrantLock(); // 线程安全锁

    private PatchUpdateListener patchUpdateListener; // 增加patch监听器

    public DocListener() {
        this.document = null;
        this.oldText = "";
        this.newText = "";
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.patch = new Patch<>();
        this.isProcessing = false;
    }

    public DocListener(Document document) {
        this();
        System.out.println("DocListener 构造函数");
        System.out.println("document: " + document);
        this.document = document;
        this.oldText = document.getText(); // 初始化时获取文档的文本内容
        this.newText = document.getText(); // 新文本内容和旧文本内容相同
    }

    @Override
    public void beforeDocumentChange(@NotNull DocumentEvent event) {
        lock.lock();// 加锁
        try {//
            if (isProcessing) {
                return; // 如果正在处理，不更新 oldText
            }
            System.out.println("获取文档" + document + "的旧文本内容");
            oldText = document.getText(); // 获取文档的旧文本内容
        } finally {
            lock.unlock();// 解锁
        }
    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        lock.lock();
        try {
            if (isProcessing) {
                return; // 如果当前有任务在执行，不再创建新的任务
            }
            // 取消之前的定时任务并启动新的缓冲任务
            // 只会有一个任务在执行
            executorService.shutdownNow(); // 取消之前的定时任务
            executorService = Executors.newSingleThreadScheduledExecutor(); // 创建新执行器
            isProcessing = true; // 标记正在处理

            // 延迟 DELAY 毫秒更新缓冲区中的文本，并生成增量 diff
            // 使用 executorService.shutdownNow() 来取消之前的定时任务，然后重新安排一个新的任务。
            // 这确保在多次快速更改时，只有最后一次的更改会被处理。
            // 这样可以避免在多次快速更改时，多次触发 diff 计算。
            executorService.schedule(() -> {
                try {
                    newText = document.getText(); // 获取最终的文本内容
                    System.out.println("Document changed from: " + oldText + " to: " + newText);

                    // 计算文本差异
                    List<String> oldLines = Arrays.asList(oldText.split("\n"));
                    List<String> newLines = Arrays.asList(newText.split("\n"));
                    patch = DiffUtils.diff(oldLines, newLines); // 计算差异

                    // 输出差异信息
                    System.out.println("差异信息：: " + patch);
                    for (var delta : patch.getDeltas()) {
                        switch (delta.getType()) {
                            case INSERT:
                                System.out.println("插入: " + delta); // 打印插入的差异
                                break;
                            case DELETE:
                                System.out.println("删除: " + delta); // 打印删除的差异
                                break;
                            case CHANGE:
                                System.out.println("更改: " + delta); // 打印更改的差异
                                break;
                        }
                    }

                    // 通知 ActionListener Patch 更新
                    if (patchUpdateListener != null) {
                        System.out.println("通知 ActionListener Patch 更新，"+ patchUpdateListener);
                        patchUpdateListener.onPatchUpdated(patch);
                    }

                    // 更新旧文本为新文本
                    oldText = newText;
                } catch (Exception e) {
                    System.err.println("差异计算出错: " + e.getMessage());
                } finally {
                    isProcessing = false; // 处理完成，重置标志
                }
            }, DELAY, TimeUnit.MILLISECONDS);
        } finally {
            lock.unlock();
        }
    }


    // 在 patch 更新后通知监听器
    public void setPatchUpdateListener(PatchUpdateListener listener) {
        this.patchUpdateListener = listener;
    }

    // 清理资源的方法
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    // 获取文本差异的方法
    public Patch<String> getPatch() {
        return patch;
    }

    // 获取文档对象的方法
    public Document getDocument() {
        return document;
    }

    // 获得当前是否更新patch的状态
    public boolean getIsProcessing() {
        return isProcessing;
    }

    public void formatPrintPatch() {
        // 获取所有的 deltas，表示每一块变化
        List<AbstractDelta<String>> deltas = patch.getDeltas();
        // 遍历每个 delta 并打印详细信息
        for (AbstractDelta<String> delta : deltas) {
            DeltaType type = delta.getType();
            Chunk<String> source = delta.getSource();
            Chunk<String> target = delta.getTarget();

            // 打印 delta 的类型 (插入, 删除, 更改)
            System.out.println("Delta type: " + type);

            // 打印 source (旧文本) 的行数和内容
            System.out.println("Source position (old text): " + source.getPosition());
            System.out.println("Source lines (old text):");
            for (String line : source.getLines()) {
                System.out.println(line);
            }

            // 打印 target (新文本) 的行数和内容
            System.out.println("Target position (new text): " + target.getPosition());
            System.out.println("Target lines (new text):");
            for (String line : target.getLines()) {
                System.out.println(line);
            }
        }
    }
}