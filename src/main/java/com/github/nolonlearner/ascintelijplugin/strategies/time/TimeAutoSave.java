package com.github.nolonlearner.ascintelijplugin.strategies.time;

import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveCondition;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveContext;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSavePriority;

/*
    * 时间自动保存条件，每隔2mins自动保存一次
    * 实现了 AutoSaveCondition 接口
    * 属性：
        AutoSavePriority priority: 优先级
    * 方法：
        shouldSave: 判断是否需要保存;
        getPriority: 返回优先级
 */

public class TimeAutoSave implements AutoSaveCondition {
    private final long saveInterval = 60*1000*2;// 2mins
    AutoSavePriority priority = AutoSavePriority.TIME;
    @Override
    public boolean shouldSave(AutoSaveContext context) {
        return false;
    }
    @Override
    public AutoSavePriority getPriority() {
        return priority;
    }
}
