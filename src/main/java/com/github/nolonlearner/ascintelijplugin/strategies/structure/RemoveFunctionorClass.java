package com.github.nolonlearner.ascintelijplugin.strategies.structure;

import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveCondition;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveContext;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSavePriority;
import com.intellij.psi.PsiElement;

public class RemoveFunctionorClass implements AutoSaveCondition {
    AutoSavePriority priority = AutoSavePriority.STRUCTURE;
    @Override
    public boolean shouldSave(AutoSaveContext context) {
        PsiElement oldChild = context.getOldChild();

        // 判断是否为函数或类
        if (isFunctionElement(oldChild)) {
            System.out.println("删除函数: " + oldChild.getText());
            return true;
        } else if (isClassElement(oldChild)) {
            System.out.println("删除类: " + oldChild.getText());
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
}
