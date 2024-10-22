package com.github.nolonlearner.ascintelijplugin.strategies.action;
// src/main/java/com/github/nolonlearner/ascintelijplugin/strategies/action/PrintReturn.java
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Chunk;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.Patch;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveCondition;

import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveContext;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSavePriority;
import com.intellij.openapi.editor.Document;

import java.util.List;

/*
    * 打印return自动保存条件
    * 实现了 AutoSaveCondition 接口
    * 属性：
        AutoSavePriority priority: 优先级
    * 方法：
        shouldSave: 判断是否打印了return;
        getPriority: 返回优先级
 */

public class PrintReturn implements AutoSaveCondition {
    AutoSavePriority priority = AutoSavePriority.ACTION;
    @Override
    public boolean shouldSave(AutoSaveContext context) {
        // 判断是否打印了return;
        Patch<String> patch = context.getPatch();
        // 获取所有的 deltas，表示每一块变化
        List<AbstractDelta<String>> deltas = patch.getDeltas();
        // 遍历每个 delta 并打印详细信息

        for (AbstractDelta<String> delta : deltas) {
            DeltaType type = delta.getType();
            Chunk<String> target = delta.getTarget();

            if (target.getLines().toString().contains("return")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public AutoSavePriority getPriority() {
        return priority; // 返回优先级
    }
}
