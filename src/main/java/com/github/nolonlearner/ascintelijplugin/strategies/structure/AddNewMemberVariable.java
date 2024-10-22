package com.github.nolonlearner.ascintelijplugin.strategies.structure;
// src/main/java/com/github/nolonlearner/ascintelijplugin/strategies/structure/AddNewMemberVariable.java
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveCondition;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveContext;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSavePriority;
import com.intellij.psi.PsiElement;

/*
    * 添加新的成员变量
    * 用于判断是否需要自动保存
 */
public class AddNewMemberVariable implements AutoSaveCondition {
    AutoSavePriority priority = AutoSavePriority.STRUCTURE;
    @Override
    public boolean shouldSave(AutoSaveContext context) {
        PsiElement newChild = context.getNewChild();
        PsiElement parent = context.getParent();
        if (isMemberVariable(newChild) && isClassElement(parent)) {
            System.out.println("类中新增成员变量: " + newChild.getText());
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
}