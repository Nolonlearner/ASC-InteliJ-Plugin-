package com.github.nolonlearner.ascintelijplugin.listeners;

import com.intellij.openapi.editor.Document;
import com.intellij.psi.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveManager;

/*
    * StructureListener 负责监听代码结构的变化
    * 根据代码结构的变化触发自动保存逻辑
    * 通过实现 PsiTreeChangeListener 接口，监听代码结构的变化
 */

public class StructureListener extends DocListener implements PsiTreeChangeListener {

    private final Project project;// 项目
    private final AutoSaveManager autoSaveManager;// 自动保存管理器
    private PsiElement psiElement;// 当前的代码结构元素

    // 构造函数
    public StructureListener(Document document, Project project, AutoSaveManager autoSaveManager) {
        super(document);
        this.project = project;
        this.autoSaveManager = autoSaveManager;
        PsiManager.getInstance(project).addPsiTreeChangeListener(this, project); // 注册监听器
    }

    // 当代码结构发生变化时触发
    private void checkStructureChange(PsiTreeChangeEvent event, String changeType) {
        System.out.println("PSI Tree changed: " + changeType);
        // 根据 event 获取更具体的信息
        PsiElement element = event.getElement();
       /* if (element instanceof PsiMethod || element instanceof PsiClass || element instanceof PsiField || element instanceof PsiImportStatementBase) {
            // 此处可以判断具体的结构类型并应用对应策略
            System.out.println("Detected structure change: " + element);
            autoSaveManager.evaluateConditions(new AutoSaveContext(null, getDocument(), element)); // 触发保存
        }*/
    }

    // 子节点增加前的事件
    @Override
    public void beforeChildAddition(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
        System.out.println("子节点增加前的事件");
        checkStructureChange(psiTreeChangeEvent, "beforeChildAddition");
    }

    // 子节点移除前的事件
    @Override
    public void beforeChildRemoval(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
        System.out.println("子节点移除前的事件");
        checkStructureChange(psiTreeChangeEvent, "beforeChildRemoval");
    }

    // 子节点替换前的事件
    @Override
    public void beforeChildReplacement(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
        System.out.println("子节点替换前的事件");
        checkStructureChange(psiTreeChangeEvent, "beforeChildReplacement");
    }

    // 子节点移动前的事件
    @Override
    public void beforeChildMovement(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
        System.out.println("子节点移动前的事件");
        checkStructureChange(psiTreeChangeEvent, "beforeChildMovement");
    }

    // 子节点改变前的事件
    @Override
    public void beforeChildrenChange(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
        System.out.println("子节点改变前的事件");
        checkStructureChange(psiTreeChangeEvent, "beforeChildrenChange");
    }

    // 属性改变前的事件
    @Override
    public void beforePropertyChange(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
        System.out.println("属性改变前的事件");
        checkStructureChange(psiTreeChangeEvent, "beforePropertyChange");
    }

    // 子节点增加后的事件
    @Override
    public void childAdded(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
        System.out.println("子节点增加后的事件");
        checkStructureChange(psiTreeChangeEvent, "childAdded");
    }

    // 子节点移除后的事件
    @Override
    public void childRemoved(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
        System.out.println("子节点移除后的事件");
        checkStructureChange(psiTreeChangeEvent, "childRemoved");
    }

    // 子节点替换后的事件
    @Override
    public void childReplaced(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
        System.out.println("子节点替换后的事件");
        checkStructureChange(psiTreeChangeEvent, "childReplaced");
    }

    // 子节点改变后的事件
    @Override
    public void childrenChanged(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
        System.out.println("子节点改变后的事件");
        checkStructureChange(psiTreeChangeEvent, "childrenChanged");
    }

    // 子节点移动后的事件
    @Override
    public void childMoved(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
        System.out.println("子节点移动后的事件");
        checkStructureChange(psiTreeChangeEvent, "childMoved");
    }

    // 属性改变后的事件
    @Override
    public void propertyChanged(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
        System.out.println("属性改变后的事件");
        checkStructureChange(psiTreeChangeEvent, "propertyChanged");
    }
}
