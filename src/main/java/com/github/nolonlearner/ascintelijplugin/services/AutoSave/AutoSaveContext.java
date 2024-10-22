package com.github.nolonlearner.ascintelijplugin.services.AutoSave;
// src/main/java/com/github/nolonlearner/ascintelijplugin/strategies/AutoSaveContext.java
import com.github.difflib.patch.Patch;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiElement;

import javax.print.Doc;

/*
    * 自动保存策略的上下文
    * 封装 Document 和 PsiElement 信息，用于条件判断。
    * 属性：
        Document document: 当前文档对象。
        PsiElement psiElement: 当前的代码结构元素。
 */
public class AutoSaveContext {
    private final Patch<String> patch;// 当前文档对象
    private final Document document;// 当前文档对象
    private final PsiElement psiElement;// 当前的代码结构元素

    public AutoSaveContext(Document document) {
        this.patch = null;
        this.document = document;
        this.psiElement = null;
    }


    // 构造函数
    public AutoSaveContext(Patch<String> patch, Document document, PsiElement psiElement) {
        this.patch = patch;
        this.psiElement = psiElement;
        this.document = document;
    }

    // getter 方法
    public PsiElement getPsiElement() {
        return psiElement;
    }

    public Patch<String> getPatch() {
        return patch;
    }
    public Document getDocument() {
        return document;
    }
}
