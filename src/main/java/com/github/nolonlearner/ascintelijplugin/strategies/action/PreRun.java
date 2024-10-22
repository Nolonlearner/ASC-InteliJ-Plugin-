package com.github.nolonlearner.ascintelijplugin.strategies.action;

import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveCondition;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveContext;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSavePriority;

/*
    * 在编译器执行前保存当前代码
 */
public class PreRun implements AutoSaveCondition {
    AutoSavePriority priority = AutoSavePriority.ACTION;
    @Override
    public boolean shouldSave(AutoSaveContext context) {
        return false;
    }

    @Override
    public AutoSavePriority getPriority() {
        return priority;
    }

}
