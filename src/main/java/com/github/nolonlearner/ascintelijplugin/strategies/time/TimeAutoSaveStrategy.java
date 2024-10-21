package com.github.nolonlearner.ascintelijplugin.strategies.time;
// src/main/java/com/github/nolonlearner/ascintelijplugin/strategies/time/TimeAutoSaveStrategy.java
/*
    * 基于时间间隔的自动保存策略。优先级设为最低。
    * 每隔一段时间触发一次保存
 */
import com.github.nolonlearner.ascintelijplugin.strategies.AutoSavePriority;
import com.github.nolonlearner.ascintelijplugin.strategies.AutoSaveStrategy;

public class TimeAutoSaveStrategy implements AutoSaveStrategy {
    private static final int DEFAULT_INTERVAL = 5 * 60 * 1000;  // 时间间隔为5分钟
    private int interval;  // 保存间隔
    private long lastSaveTime;  // 上次保存时间
    AutoSavePriority Priority = AutoSavePriority.LOW;  // 优先级为低

    public TimeAutoSaveStrategy() {
        this(DEFAULT_INTERVAL);// 默认构造函数
    }

    public TimeAutoSaveStrategy(int interval) {// 构造函数
        this.interval = interval;// 传入的时间间隔
        this.lastSaveTime = System.currentTimeMillis();// 初始化上次保存时间
    }

    @Override
    public void triggerSave() {
        System.out.println("TimeAutoSaveStrategy: 触发保存");
        // 下面实现保存的机制

        lastSaveTime = System.currentTimeMillis();// 保存当前时间
    }

    @Override
    public boolean shouldTrigger() {
        return System.currentTimeMillis() - lastSaveTime >= interval;// 判断是否满足触发条件
    }

    @Override
    public void reset() {
        lastSaveTime = System.currentTimeMillis();
    }

    @Override
    public AutoSavePriority getPriority() {
        return Priority;
    }
}
