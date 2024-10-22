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
    private final PsiElement oldChild;
    private final PsiElement newChild;
    private final PsiElement parent;
    private final PsiElement child;


    public AutoSaveContext(Document document) {
        this.patch = null;
        this.document = document;
        this.psiElement = null;
        this.oldChild = null;
        this.newChild = null;
        this.parent = null;
        this.child = null;
    }


    // 构造函数
    public AutoSaveContext(Patch<String> patch, Document document, PsiElement psiElement) {
        this.patch = patch;
        this.psiElement = psiElement;
        this.document = document;
        this.oldChild = null;
        this.newChild = null;
        this.parent = null;
        this.child = null;
    }

    // 构造函数
    public AutoSaveContext(Patch<String> patch, Document document, PsiElement oldChild, PsiElement newChild, PsiElement parent, PsiElement child, PsiElement psiElemen) {
        this.patch = patch;
        this.document = document;
        this.oldChild = oldChild;
        this.newChild = newChild;
        this.parent = parent;
        this.child = child;
        this.psiElement = psiElemen;
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

    public PsiElement getOldChild() {
        return oldChild;
    }

    public PsiElement getNewChild() {
        return newChild;
    }

    public PsiElement getParent() {
        return parent;
    }

    public PsiElement getChild() {
        return child;
    }
}
