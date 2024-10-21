package com.github.nolonlearner.ascintelijplugin.listeners;
// src/main/java/com/github/nolonlearner/ascintelijplugin/listeners/ActionListener.java

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
 */
public class ActionListener extends DocListener {

    private final AutoSaveManager autoSaveManager;

    public ActionListener(Document document, AutoSaveManager autoSaveManager) {
        super(document); // 调用父类构造函数
        this.autoSaveManager = autoSaveManager;

        // 注册关于用户动作的条件
        autoSaveManager.addCondition(new LineCount(10));// 如果写代码超过十行
        autoSaveManager.addCondition(new PrintReturn());// 如果打印了return;
    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        // 当检测到文档变化时
        super.documentChanged(event); // 调用父类的 documentChanged 方法
        System.out.println("ActionListener.documentChanged");// 输出信息
        // 获取当前文档和 PsiElement
        Document document = event.getDocument();
        PsiElement psiElement = getCurrentPsiElement(document); // 假设实现这个方法

        // 创建上下文并评估条件
        AutoSaveContext context = new AutoSaveContext(document, psiElement);
        //System.out.println("context in ActionListener:"+ context.getDocument().getText());
        autoSaveManager.evaluateConditions(context);
    }

    private PsiElement getCurrentPsiElement(Document document) {
        // 实现代码以获取当前 PsiElement
        return null; // 这里需要根据具体上下文实现
    }
}