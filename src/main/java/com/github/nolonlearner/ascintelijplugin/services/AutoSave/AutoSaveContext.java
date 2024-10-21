package com.github.nolonlearner.ascintelijplugin.services.AutoSave;
// src/main/java/com/github/nolonlearner/ascintelijplugin/strategies/AutoSaveContext.java
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiElement;
/*
    * 自动保存策略的上下文
    * 封装 Document 和 PsiElement 信息，用于条件判断。
    * 属性：
        Document document: 当前文档对象。
        PsiElement psiElement: 当前的代码结构元素。
 */
public class AutoSaveContext {
    private final Document document;// 当前文档对象
    private final PsiElement psiElement;// 当前的代码结构元素

    // 构造函数
    public AutoSaveContext(Document document, PsiElement psiElement) {
        this.document = document;
        this.psiElement = psiElement;
    }

    // getter 方法
    public Document getDocument() {
        return document;
    }

    public PsiElement getPsiElement() {
        return psiElement;
    }
}
