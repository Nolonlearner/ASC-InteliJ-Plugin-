package com.github.nolonlearner.ascintelijplugin.listeners;

import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveChangeType;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveContext;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveManager;
import com.github.nolonlearner.ascintelijplugin.strategies.time.TimeAutoSave;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
 * TimeListener 负责监听并注册时间策略
 * 只负责监听时间间隔，不涉及具体的自动保存逻辑
 */
public class TimeListener extends DocListener {

    private final AutoSaveManager autoSaveManager;
    private final ScheduledExecutorService scheduler;

    public TimeListener(Document document, AutoSaveManager autoSaveManager) {
        super(document);
        this.autoSaveManager = autoSaveManager;
        scheduler = Executors.newScheduledThreadPool(1);

        // 注册基于时间间隔的自动保存策略
        autoSaveManager.addCondition(new TimeAutoSave());

        // 开启定时任务
        startScheduledTask();
    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        // 文档变化时的行为，暂不处理具体变化
        super.documentChanged(event);
        System.out.println("TimeListener.documentChanged");
    }

    private void startScheduledTask() {
        scheduler.scheduleAtFixedRate(() -> {
            Document document = getDocument();
            AutoSaveContext context = new AutoSaveContext(document, AutoSaveChangeType.TIME);
            // 在每个固定时间间隔检查是否需要保存
            System.out.println("TimeListener 触发定时保存逻辑");
            autoSaveManager.evaluateConditions(context);
        }, 5, 1, TimeUnit.MINUTES); // 每1分钟检查一次, 5秒后开始第一次检查
    }

    // 关闭调度器以避免内存泄漏
    public void shutdown() {
        if (!scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
}
