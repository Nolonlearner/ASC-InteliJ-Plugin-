package com.github.nolonlearner.ascintelijplugin.strategies.structure;

import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveChangeType;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveCondition;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveContext;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSavePriority;
import com.intellij.psi.PsiElement;

/*
    * 添加新的函数
    * 用于判断是否需要自动保存
 */
public class AddFunctionorClass implements AutoSaveCondition {
    AutoSavePriority priority = AutoSavePriority.HIGHEST;
    @Override
    public boolean shouldSave(AutoSaveContext context) {
        PsiElement newChild = context.getNewChild();
        PsiElement parent = context.getParent();
        if (isFunctionElement(newChild)) {
            System.out.println("新增函数: " + newChild.getText());
            return true;
        } else if (isClassElement(newChild)) {
            System.out.println("新增类: " + newChild.getText());
            return true;
        }
        return false;
    }

    @Override
    public AutoSavePriority getPriority() {
        return priority;
    }

    private boolean isClassElement(PsiElement element) {
        // 先检查 element 是否为 null
        if (element == null) {
            return false; // 如果 element 是 null，直接返回 false，表示它不是函数元素
        }
        // 判断是否为类元素的逻辑
        return element != null && element.getText().startsWith("class ");
    }

    private boolean isFunctionElement(PsiElement element) {
        // 先检查 element 是否为 null
        if (element == null) {
            return false; // 如果 element 是 null，直接返回 false，表示它不是函数元素
        }
        // 这里根据具体的实现判断是否为函数元素，可以根据节点类型判断
        return element != null && element.getText().contains("void")
                || element.getText().contains("int")
                || element.getText().contains("String")
                || element.getText().contains("double")
                || element.getText().contains("float")
                || element.getText().contains("char")
                || element.getText().contains("boolean")
                || element.getText().contains("long")
                || element.getText().contains("short")
                || element.getText().contains("byte");
    }
    // 重写 isRelevant 方法
    @Override
    public boolean isRelevant(AutoSaveChangeType changeType) {
        return changeType == AutoSaveChangeType.STRUCTURE;
    }
}