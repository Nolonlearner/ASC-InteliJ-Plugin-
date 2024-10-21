package com.github.nolonlearner.ascintelijplugin.strategies.action;
// src/main/java/com/github/nolonlearner/ascintelijplugin/strategies/action/LineCount.java
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveCondition;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSavePriority;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveContext;
/*

 */
public class LineCount implements AutoSaveCondition {
    AutoSavePriority priority = AutoSavePriority.ACTION;
    private int lineThreshold;// 行数阈值

    public LineCount() {
        // 默认行数阈值
        this.lineThreshold = 10;// 默认行数阈值为10
    }

    public LineCount(int lineThreshold) {
        this.lineThreshold = lineThreshold;// 行数阈值
    }

    @Override
    public boolean shouldSave(AutoSaveContext context) {
        // 判断是否出现更多的行数的变化

        return false;
    }

    @Override
    public AutoSavePriority getPriority() {
        return AutoSavePriority.ACTION;
    }

    public int getLineThreshold() {
        return lineThreshold;
    }
}
