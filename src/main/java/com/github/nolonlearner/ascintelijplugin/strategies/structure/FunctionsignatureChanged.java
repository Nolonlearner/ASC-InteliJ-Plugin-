package com.github.nolonlearner.ascintelijplugin.strategies.structure;

import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveChangeType;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveCondition;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveContext;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSavePriority;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;

public class FunctionsignatureChanged implements AutoSaveCondition {
    private final AutoSavePriority priority = AutoSavePriority.HIGHEST;

    @Override
    public boolean shouldSave(AutoSaveContext context) {
        PsiElement oldChild = context.getOldChild();
        PsiElement newChild = context.getNewChild();

        // 检查是否涉及函数签名的部分
        if (isFunctionSignatureElement(oldChild) || isFunctionSignatureElement(newChild)) {
            System.out.println("函数签名发生变化");
            return true;
        }
        return false;
    }

    @Override
    public AutoSavePriority getPriority() {
        return priority;
    }

    private boolean isFunctionSignatureElement(PsiElement element) {
        if (element == null) {
            return false;
        }

        // 使用 PSI 工具类来确定该元素是否为函数签名相关的元素
        // 例如检查函数的参数列表、返回类型或函数名称是否有变化
        return isReturnTypeChange(element) || isParameterListChange(element) || isFunctionNameChange(element);
    }

    private boolean isReturnTypeChange(PsiElement element) {
        // 先检查 element 是否为 null
        if (element == null) {
            return false; // 如果 element 是 null，直接返回 false，表示它不是函数元素
        }
        // 检查返回类型的变化，可以通过更具体的上下文分析返回类型的子节点
        return element.getText().matches("void|int|String|boolean|double|float|char|long|short|byte");
    }

    private boolean isParameterListChange(PsiElement element) {
        // 先检查 element 是否为 null
        if (element == null) {
            return false; // 如果 element 是 null，直接返回 false，表示它不是函数元素
        }
        // 使用 PSI 来分析函数的参数部分是否发生变化，可以通过子节点判断参数的改变
        return element.getText().contains("(") && element.getText().contains(")");
    }

    private boolean isFunctionNameChange(PsiElement element) {
        // 先检查 element 是否为 null
        if (element == null) {
            return false; // 如果 element 是 null，直接返回 false，表示它不是函数元素
        }
        // 识别是否是函数名称的变化，这部分可以通过 PsiNamedElement 检测
        return element instanceof PsiNamedElement;
    }

    // 重写 isRelevant 方法
    @Override
    public boolean isRelevant(AutoSaveChangeType changeType) {
        return changeType == AutoSaveChangeType.STRUCTURE;
    }
}
