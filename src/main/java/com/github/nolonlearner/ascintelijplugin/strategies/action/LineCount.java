package com.github.nolonlearner.ascintelijplugin.strategies.action;
// src/main/java/com/github/nolonlearner/ascintelijplugin/strategies/action/LineCount.java
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Chunk;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.Patch;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveCondition;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSavePriority;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveContext;

import java.util.List;

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
    private final int LINETHRESHOLD = 6;// 行数阈值
    @Override
    public boolean shouldSave(AutoSaveContext context) {
        // 判断是否出现更多的行数的变化
        Patch<String> patch = context.getPatch();
        // 获取所有的 deltas，表示每一块变化
        List<AbstractDelta<String>> deltas = patch.getDeltas();
        // 遍历每个 delta 并打印详细信息

        // 判断行数变化
        int lineChange = 0;

        for (AbstractDelta<String> delta : deltas) {
            DeltaType type = delta.getType();
            Chunk<String> source = delta.getSource();
            Chunk<String> target = delta.getTarget();
            if (type == DeltaType.INSERT) {
                lineChange += target.size();  // 插入行数
            } else if (type == DeltaType.DELETE) {
                lineChange += source.size();  // 删除行数
            } else if (type == DeltaType.CHANGE) {
                // 如果是修改类型，行数没有变化，记录修改内容即可
                System.out.println("行内容被修改！");
            }
        }

       /* System.out.println("------------------------------");
        System.out.println("Line change: " + Math.abs(lineChange));
        System.out.println("------------------------------");*/
        return Math.abs(lineChange) >= LINETHRESHOLD;

    }

    @Override
    public AutoSavePriority getPriority() {
        return priority;
    }

    public int getLineThreshold() {
        return LINETHRESHOLD;
    }
}
