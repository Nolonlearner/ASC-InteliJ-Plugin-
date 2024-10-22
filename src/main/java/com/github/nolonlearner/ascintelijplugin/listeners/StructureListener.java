package com.github.nolonlearner.ascintelijplugin.listeners;

import com.github.difflib.patch.Patch;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveContext;
import com.github.nolonlearner.ascintelijplugin.strategies.structure.*;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.file.*;
import com.intellij.psi.PsiFile;
import com.intellij.psi.*;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.PsiTreeChangeListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/*
    * StructureListener 负责监听代码结构的变化
    * 根据代码结构的变化触发自动保存逻辑
    * 通过实现 PsiTreeChangeListener 接口，监听代码结构的变化
 */

public class StructureListener extends DocListener implements PsiTreeChangeListener {

    private final Project project;// 项目
    private final AutoSaveManager autoSaveManager;// 自动保存管理器
    private PsiElement psiElement;// 当前的代码结构元素
    private Timer timer;
    private List<PsiTreeChangeEvent> changes = new ArrayList<>();

    // 构造函数
    public StructureListener(Document document, Project project, AutoSaveManager autoSaveManager) {
        super(document);
        this.project = project;
        this.autoSaveManager = autoSaveManager;
        PsiManager.getInstance(project).addPsiTreeChangeListener(this, project); // 注册监听器

        autoSaveManager.addCondition(new FunctionsignatureChanged());// 函数签名的修改
        autoSaveManager.addCondition(new AddFunctionorClass());// 新增函数、类
        autoSaveManager.addCondition(new RemoveFunctionorClass());// 删除函数、类
        autoSaveManager.addCondition(new RefactoringorMovingofCodeblocks());// 重构或移动代码块
        autoSaveManager.addCondition(new AddNewMemberVariable());// 新增成员变量
        autoSaveManager.addCondition(new DeleteMemberVariable());// 删除成员变量
        timer = new Timer();// 定时器
    }
    // 当代码结构发生变化时触发
    private void checkStructureChange(PsiTreeChangeEvent event, String changeType) {
        // 根据 event 获取更具体的信息
        PsiElement psielement = event.getElement();
        PsiElement oldChild = event.getOldChild();
        PsiElement newChild = event.getNewChild();
        PsiElement parent = event.getParent();
        PsiElement child = event.getChild();
        Patch<String> patch = getPatch();
        Document document = getDocument();
        AutoSaveContext context = new AutoSaveContext(patch, document, oldChild, newChild, parent, child, psiElement);
        autoSaveManager.evaluateConditions(context);// 根据 Patch 和 psi 变化进行策略判断和保存操作
    }

    private void collectChange(PsiTreeChangeEvent event) {
        changes.add(event); // 收集变化
        resetTimer(); // 重置定时器
    }

    private void resetTimer() {
        // 如果已经有定时器在运行，取消它
        if (timer != null) {
            timer.cancel();
        }

        // 创建新的定时器任务
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                processChanges(); // 处理变化
            }
        }, 5000); // 5秒后执行
    }

    private void processChanges() {
        // 处理收集到的变化
        Patch<String> patch = getPatch();
        Document document = getDocument();
        AutoSaveContext context = new AutoSaveContext(patch, document, null, null, null, null, null);
        System.out.println("changes.size(): "+ changes.size());
        // 遍历变化并进行条件评估
        /*for (PsiTreeChangeEvent change : changes) {
            // 在这里可以根据变化类型进行处理，或直接传递给AutoSaveManager
            checkStructureChange(change, "processChanges");

        }*/

        // 清空变化列表
        changes.clear();

        // 最后评估条件
        // autoSaveManager.evaluateConditions(context);
    }

    // 子节点移除前的事件
    @Override
    public void beforeChildRemoval(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
        //checkStructureChange(psiTreeChangeEvent, "beforeChildRemoval");
        collectChange(psiTreeChangeEvent);
    }

    // 子节点替换前的事件
    @Override
    public void beforeChildReplacement(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
        //checkStructureChange(psiTreeChangeEvent, "beforeChildReplacement");
        collectChange(psiTreeChangeEvent);
    }

    // 子节点移动前的事件
    @Override
    public void beforeChildMovement(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
        //checkStructureChange(psiTreeChangeEvent, "beforeChildMovement");
        collectChange(psiTreeChangeEvent);
    }

    // 子节点改变前的事件
    @Override
    public void beforeChildrenChange(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
        //checkStructureChange(psiTreeChangeEvent, "beforeChildrenChange");
        collectChange(psiTreeChangeEvent);
    }

    // 属性改变前的事件
    @Override
    public void beforePropertyChange(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
        //checkStructureChange(psiTreeChangeEvent, "beforePropertyChange");
        collectChange(psiTreeChangeEvent);
    }

    // 子节点增加后的事件
    @Override
    public void childAdded(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
        //checkStructureChange(psiTreeChangeEvent, "childAdded");
        collectChange(psiTreeChangeEvent);
    }

    // 子节点移除后的事件
    @Override
    public void childRemoved(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
        //checkStructureChange(psiTreeChangeEvent, "childRemoved");
        collectChange(psiTreeChangeEvent);
    }

    // 子节点替换后的事件
    @Override
    public void childReplaced(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
        //checkStructureChange(psiTreeChangeEvent, "childReplaced");
        collectChange(psiTreeChangeEvent);
    }

    // 子节点改变后的事件
    @Override
    public void childrenChanged(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
        //checkStructureChange(psiTreeChangeEvent, "childrenChanged");
        collectChange(psiTreeChangeEvent);
    }

    // 子节点移动后的事件
    @Override
    public void childMoved(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
        //checkStructureChange(psiTreeChangeEvent, "childMoved");
        collectChange(psiTreeChangeEvent);
    }

    // 属性改变后的事件
    @Override
    public void propertyChanged(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
        //checkStructureChange(psiTreeChangeEvent, "propertyChanged");
        collectChange(psiTreeChangeEvent);
    }

    @Override
    public void beforeChildAddition(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
        //checkStructureChange(psiTreeChangeEvent, "beforeChildAddition");
        collectChange(psiTreeChangeEvent);
    }
}
