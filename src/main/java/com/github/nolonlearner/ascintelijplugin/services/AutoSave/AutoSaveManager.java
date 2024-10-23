package com.github.nolonlearner.ascintelijplugin.services.AutoSave;
// src/main/java/com/github/nolonlearner/ascintelijplugin/services/AutoSave/AutoSaveManager.java

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*
    * 自动保存管理器
    * 用于管理自动保存条件，评估条件并执行保存
 */

import com.github.nolonlearner.ascintelijplugin.toolWindow.VersionControlToolWindowFactory;

public class AutoSaveManager {
    private final List<AutoSaveCondition> conditions;// 保存所有的自动保存条件
    private String lastSavedContent; // 存储上一次保存的文档内容
    private AutoSaveCondition hitedCondition; // 保存命中的条件
    private Project project; // 项目实例
    private VersionControlToolWindowFactory versionControlToolWindowFactory; // 工具窗口工厂的实例

    public AutoSaveManager(Project project) {
        this.conditions = new ArrayList<>();
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
        // 根据变化类型选择需要检查的保存策略
        AutoSaveChangeType changeType = context.getChangeType();
        List<AutoSaveCondition> relevantConditions = conditions.stream()
                .filter(condition -> condition.isRelevant(changeType))  // 过滤与当前变化类型相关的条件
                .sorted((condition1, condition2) ->
                        Integer.compare(condition2.getPriority().ordinal(), condition1.getPriority().ordinal()))
                .collect(Collectors.toList());


        // 检查内容是否有变化
        Document document = context.getDocument();
        String text = document.getText();

        if (!text.equals(lastSavedContent)) {
            // 遍历条件并执行保存
            for (AutoSaveCondition condition : relevantConditions) {
                if (condition.shouldSave(context)) {
                    lastSavedContent = text; // 更新最后保存的内容
                    hitedCondition = condition; // 更新命中的条件
                    // 保存后退出循环
                    versionControlToolWindowFactory.saveProjectVersion(this.project);// 对应的保存项目版本 接口
                    break; // 一旦找到满足条件的策略，退出循环
                }
            }
            if (hitedCondition != null) {
                System.out.println("！！！本次命中的条件是：" + hitedCondition.getClass().getSimpleName());
                System.out.println("-----------------------------------------------");
                hitedCondition = null;
            }
        }

    }
}