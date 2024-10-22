package com.github.nolonlearner.ascintelijplugin.services.AutoSave;
// src/main/java/com/github/nolonlearner/ascintelijplugin/services/AutoSave/AutoSaveManager.java

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.github.nolonlearner.ascintelijplugin.toolWindow.VersionControlToolWindowFactory;

public class AutoSaveManager {
    private final List<AutoSaveCondition> conditions;// 保存所有的自动保存条件
    private final SaveCommand saveCommand;// 保存命令
    private String lastSavedContent; // 存储上一次保存的文档内容
    private AutoSaveCondition hitedCondition; // 保存命中的条件
    private Project project; // 项目实例
    private VersionControlToolWindowFactory versionControlToolWindowFactory; // 工具窗口工厂的实例

    public AutoSaveManager(SaveCommand saveCommand, Project project) {
        this.conditions = new ArrayList<>();
        this.saveCommand = saveCommand;
        this.lastSavedContent = ""; // 初始为空
        hitedCondition = null;
        this.project = project;

        // 实例化 VersionControlToolWindowFactory
        this.versionControlToolWindowFactory = new VersionControlToolWindowFactory();

    }

    public void addCondition(AutoSaveCondition condition) {
        conditions.add(condition);
    }


    public void evaluateConditions(AutoSaveContext context) {// 评估条件
        // 确保 context 不为 null
        if (context == null) {
            return; // 或者提供默认的上下文来处理无文档情况
        }
        // 根据优先级排序条件，高优先级优先

        conditions.sort((condition1, condition2) ->
                Integer.compare(condition2.getPriority().ordinal(), condition1.getPriority().ordinal()));

        // 检查内容是否有变化
        Document document = context.getDocument();
        String text = document.getText();
        if (!text.equals(lastSavedContent)) {
            // 遍历条件并执行保存
            for (AutoSaveCondition condition : conditions) {
                if (condition.shouldSave(context)) {
                    saveCommand.execute(document);
                    lastSavedContent = text; // 更新最后保存的内容
                    hitedCondition = condition; // 更新命中的条件
                    // 保存后退出循环
                    versionControlToolWindowFactory.saveCurrentVersion(this.project);
                    break; // 一旦找到满足条件的策略，退出循环
                }
            }
        }
        if (hitedCondition != null) {
            System.out.println("本次命中的条件是：" + hitedCondition.getClass().getSimpleName());
            hitedCondition = null;
        }
    }
}