package com.github.nolonlearner.ascintelijplugin.strategies.structure;

import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveCondition;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveContext;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSavePriority;

public class SaveOnStructureChangeCondition implements AutoSaveCondition {
    @Override
    public boolean shouldSave(AutoSaveContext context) {
        // 判断是否有结构性的变化
        return false;
    }

    @Override
    public AutoSavePriority getPriority() {
        return AutoSavePriority.STRUCTURE;
    }
}
