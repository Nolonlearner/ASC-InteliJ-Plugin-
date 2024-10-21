package com.github.nolonlearner.ascintelijplugin.strategies.action;
// src/main/java/com/github/nolonlearner/ascintelijplugin/strategies/action/LineCount.java
import com.github.difflib.patch.Patch;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveCondition;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSavePriority;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveContext;

/*
    * 行数自动保存条件
    * 实现了 AutoSaveCondition 接口
    * 属性：
        AutoSavePriority priority: 优先级
        int LINETHRESHOLD: 行数阈值
    * 方法：
        shouldSave: 判断是否出现更多的行数的变化;
        getPriority: 返回优先级
 */
public class LineCount implements AutoSaveCondition {
    AutoSavePriority priority = AutoSavePriority.ACTION;
    private final int LINETHRESHOLD = 5;// 行数阈值
    @Override
    public boolean shouldSave(AutoSaveContext context) {
        // 判断是否出现更多的行数的变化
        Patch<String> patch = context.getPatch();
        int lineChange = patch.toString().split("\n").length;
        System.out.println("LineCount: 行数变化为" + lineChange);
        return lineChange > LINETHRESHOLD;
    }

    @Override
    public AutoSavePriority getPriority() {
        return priority;
    }

    public int getLineThreshold() {
        return LINETHRESHOLD;
    }
}
