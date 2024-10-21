package com.github.nolonlearner.ascintelijplugin.strategies;
// src/main/java/com/github/nolonlearner/ascintelijplugin/strategies/AutoSaveStrategy.java
/*
 * 自动保存策略接口
 * 规定了每个策略都需要实现的方法
 */

public interface AutoSaveStrategy {
    void triggerSave();  // 触发保存的方法
    boolean shouldTrigger();  // 判断是否满足触发条件
    void reset();  // 重置当前策略的状态
    AutoSavePriority getPriority();  // 返回当前策略的优先级
}
