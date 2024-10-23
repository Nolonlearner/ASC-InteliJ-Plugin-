package com.github.nolonlearner.ascintelijplugin.strategies.structure;

import com.github.difflib.patch.Patch;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveChangeType;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveCondition;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveContext;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSavePriority;
import com.intellij.psi.PsiElement;

public class RefactoringorMovingofCodeblocks implements AutoSaveCondition {
    AutoSavePriority priority = AutoSavePriority.HIGHEST;

    @Override
    public boolean shouldSave(AutoSaveContext context) {
        PsiElement psiElement = context.getPsiElement();
        Patch<String> patch = context.getPatch();

        // 如果 psiElement 或 patch 为空，直接返回 false
        if (psiElement == null || patch == null) {
            return false;
        }

        // 判断代码块是否被重构或移动
        if (isRefactoredOrMoved(psiElement, patch)) {
            return true; // 检测到重构或移动
        }

        return false;
    }

    @Override
    public AutoSavePriority getPriority() {
        return priority;
    }

    // 判断代码块是否被重构或移动
    private boolean isRefactoredOrMoved(PsiElement element, Patch<String> patch) {
        // 先检查 element 是否为 null
        if (element == null) {
            return false; // 如果 element 是 null，直接返回 false，表示它不是函数元素
        }
        // 获取 PsiElement 的文本范围信息，通常可以通过 startOffset 和 endOffset 获取位置信息
        int startOffset = element.getTextRange().getStartOffset();
        int endOffset = element.getTextRange().getEndOffset();

        // 遍历 Patch 中的增量变化，检查是否涉及该 PsiElement 的位置
        // 这里只是一个简单示例，实际中可以进一步解析 Patch 中的 diff 信息
        if (isElementMoved(startOffset, endOffset, patch)) {
            return true; // 如果增量变化影响了元素的位置，则认为是移动或重构
        }

        return false;
    }

    // 判断 PsiElement 是否因为重构或移动而改变了位置
    private boolean isElementMoved(int startOffset, int endOffset, Patch<String> patch) {
        // 遍历 Patch 的修改信息，检查是否涉及到 startOffset 和 endOffset 位置的代码
        // 具体实现可以分析 patch.getDeltas()，获取每个增量的变化范围
        return patch.getDeltas().stream().anyMatch(delta -> {
            // 检查每个增量是否影响了 PsiElement 的文本范围
            int deltaStart = delta.getSource().getPosition();
            int deltaEnd = deltaStart + delta.getSource().size();

            // 如果 delta 涉及了 PsiElement 的文本范围，认为代码块被移动或重构
            return (deltaStart <= startOffset && deltaEnd >= endOffset)
                    || (startOffset <= deltaStart && endOffset >= deltaEnd);
        });
    }

    // 重写 isRelevant 方法
    @Override
    public boolean isRelevant(AutoSaveChangeType changeType) {
        return changeType == AutoSaveChangeType.STRUCTURE;
    }
}
