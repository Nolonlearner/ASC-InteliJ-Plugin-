package com.github.nolonlearner.ascintelijplugin.strategies.time;

import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveCondition;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveContext;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSavePriority;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeAutoSave implements AutoSaveCondition {

    private final long saveInterval = 1* 60 * 1000; // 1分钟的保存间隔
    private final AutoSavePriority priority = AutoSavePriority.TIME;
    private long lastSaveTime = 0; // 上一次保存的时间戳

    @Override
    public boolean shouldSave(AutoSaveContext context) {
        long currentTime = System.currentTimeMillis();

        // 判断是否达到保存间隔
        if (currentTime - lastSaveTime >= saveInterval) {
            lastSaveTime = currentTime; // 更新保存时间
            System.out.println("达到时间间隔，触发自动保存");
            return true; // 返回 true 表示应该进行保存
        }
        System.out.println("未达到时间间隔，不保存");
        return false; // 未达到时间间隔，不保存
    }

    @Override
    public AutoSavePriority getPriority() {
        return priority; // 返回基于时间的优先级
    }
}
