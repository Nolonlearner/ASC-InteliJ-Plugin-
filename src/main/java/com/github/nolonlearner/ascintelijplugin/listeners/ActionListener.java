package com.github.nolonlearner.ascintelijplugin.listeners;
// src/main/java/com/github/nolonlearner/ascintelijplugin/listeners/ActionListener.java

import com.github.difflib.patch.Patch;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveManager;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveContext;
import com.github.nolonlearner.ascintelijplugin.strategies.action.LineCount;
import com.github.nolonlearner.ascintelijplugin.strategies.action.PrintReturn;
import com.github.nolonlearner.ascintelijplugin.strategies.structure.SaveOnStructureChangeCondition;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/*
    * 继承自 DocListener
    * 监听用户的操作行为，根据条件判断是否执行自动保存操作。
    *
 */
public class ActionListener extends DocListener implements PatchUpdateListener{

    private final AutoSaveManager autoSaveManager;
    private PsiElement psiElement;// 当前的代码结构元素

    public ActionListener(Document document, AutoSaveManager autoSaveManager) {
        super(document); // 调用父类构造函数
        this.autoSaveManager = autoSaveManager;

        // 注册关于用户动作的条件
        autoSaveManager.addCondition(new LineCount());// 如果写代码超过阈值行数
        autoSaveManager.addCondition(new PrintReturn());// 如果打印了return;
        // 注册 Patch 更新回调
        setPatchUpdateListener(this); // 将自己作为 PatchUpdateListener 传递给 DocListener
    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        // 当检测到文档变化时也不会执行保存逻辑，直到 Patch 更新
        super.documentChanged(event); // 调用父类的 documentChanged 方法
        System.out.println("ActionListener.documentChanged");

    }

    private PsiElement getCurrentPsiElement(Document document) {
        // 实现代码以获取当前 PsiElement
        return null; // 这里需要根据具体上下文实现
    }

    @Override
    public void onPatchUpdated(Patch<String> patch) {
        // 当 DocListener 更新 Patch 时，触发策略判断
        System.out.println("ActionListener 检测到 Patch 更新，开始评估条件。");
        // 创建上下文并评估条件
        Document document = getDocument();
        AutoSaveContext context = new AutoSaveContext(patch, document, psiElement);
        autoSaveManager.evaluateConditions(context); // 根据 Patch 和 psi 变化进行策略判断和保存操作

    }
}