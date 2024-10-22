package com.github.nolonlearner.ascintelijplugin.strategies.action;

import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Chunk;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.Patch;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveCondition;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveContext;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSavePriority;

import java.util.List;
/*
    * 编辑代码块自动保存条件
    * 当输入{}后，编辑代码块超过5行时触发保存
    * 实现了 AutoSaveCondition 接口
    * 属性：
        AutoSavePriority priority: 优先级
        int linesEdited: 编辑的行数
        int THRESHOLD: 触发保存的阈值
    * 方法：
        shouldSave: 判断是否编辑了代码块;
        getPriority: 返回优先级
 */

public class BlockEdit implements AutoSaveCondition {
    private AutoSavePriority priority = AutoSavePriority.ACTION;
    private int linesEdited = 0;
    private final int THRESHOLD = 5;

    @Override
    public boolean shouldSave(AutoSaveContext context) {
        Patch<String> patch = context.getPatch();
        List<AbstractDelta<String>> deltas = patch.getDeltas();
        // 遍历每个 delta 并打印详细信息

        // 判断行数变化
        int linesEdited = 0;

        for (AbstractDelta<String> delta : deltas) {
            DeltaType type = delta.getType();
            Chunk<String> source = delta.getSource();
            Chunk<String> target = delta.getTarget();
            if (type == DeltaType.INSERT) {
                linesEdited += target.size();  // 插入行数
            } else if (type == DeltaType.DELETE) {
                linesEdited -= source.size();  // 删除行数
            } else if (type == DeltaType.CHANGE) {
                // 如果是修改类型，行数没有变化，记录修改内容即可
                System.out.println("Block Edit:行内容被修改！");
            }

            if (target.getLines().toString().contains("{")&&target.getLines().toString().contains("}")) {
                if(Math.abs(linesEdited)>=THRESHOLD) {
                    linesEdited = 0; // 重置计数器
                    return true;
                }
            }
        }

        // System.out.println("BlockEdited Lines edited: " + linesEdited);
        return false;
    }

    @Override
    public AutoSavePriority getPriority() {
        return priority;
    }
}
