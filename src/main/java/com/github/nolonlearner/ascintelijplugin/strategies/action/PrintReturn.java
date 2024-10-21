package com.github.nolonlearner.ascintelijplugin.strategies.action;
// src/main/java/com/github/nolonlearner/ascintelijplugin/strategies/action/PrintReturn.java
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveCondition;

import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveContext;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSavePriority;
import com.intellij.openapi.editor.Document;

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
        Document document = context.getDocument();
        String text = document.getText();
        return text.contains("return;");
    }

    @Override
    public AutoSavePriority getPriority() {
        return priority; // 返回优先级
    }
}
