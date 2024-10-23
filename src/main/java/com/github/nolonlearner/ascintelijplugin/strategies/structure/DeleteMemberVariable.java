package com.github.nolonlearner.ascintelijplugin.strategies.structure;
// src/main/java/com/github/nolonlearner/ascintelijplugin/strategies/structure/DeleteMemberVariable.java
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveChangeType;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveCondition;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveContext;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSavePriority;
import com.intellij.psi.PsiElement;

public class DeleteMemberVariable implements AutoSaveCondition {
    AutoSavePriority priority = AutoSavePriority.HIGH;
    @Override
    public boolean shouldSave(AutoSaveContext context) {
        PsiElement oldChild = context.getOldChild();
        PsiElement parent = context.getParent();
        if (isMemberVariable(oldChild) && isClassElement(parent)) {
            System.out.println("删除成员变量: " + oldChild.getText());
            return true;
        }
        return false;
    }

    @Override
    public AutoSavePriority getPriority() {
        return priority;
    }

    private boolean isMemberVariable(PsiElement element) {
        // 先检查 element 是否为 null
        if (element == null) {
            return false; // 如果 element 是 null，直接返回 false，表示它不是函数元素
        }
        // 判断是否为成员变量的逻辑
        return element != null && element.getText().contains("=");
    }

    private boolean isClassElement(PsiElement element) {
        // 先检查 element 是否为 null
        if (element == null) {
            return false; // 如果 element 是 null，直接返回 false，表示它不是函数元素
        }
        // 判断是否为类的逻辑
        return element != null && element.getText().contains("class");
    }
    // 重写 isRelevant 方法
    @Override
    public boolean isRelevant(AutoSaveChangeType changeType) {
        return changeType == AutoSaveChangeType.STRUCTURE;
    }
}