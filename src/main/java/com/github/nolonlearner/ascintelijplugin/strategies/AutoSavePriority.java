package com.github.nolonlearner.ascintelijplugin.strategies;
// src/main/java/com/github/nolonlearner/ascintelijplugin/strategies/AutoSavePriority.java
/*
    * 自动保存的优先级
 */
public enum AutoSavePriority {
    LOW,      // 低优先级，时间间隔
    MEDIUM,   // 中等优先级，特定行为
    HIGH,     // 高优先级，结构变化
}