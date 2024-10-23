package com.github.nolonlearner.ascintelijplugin.services.AutoSave;

/*
    * 定义自动保存条件的标准接口
    * 优先级越高，越先执行
    * 这个接口是每一个自动保存策略都要实现的
 */

public interface AutoSaveCondition {
    boolean shouldSave(AutoSaveContext context);// 判断是否需要保存
    AutoSavePriority getPriority(); // 返回条件的优先级

    // 判断该条件是否与当前变化类型相关
    boolean isRelevant(AutoSaveChangeType changeType);
}
