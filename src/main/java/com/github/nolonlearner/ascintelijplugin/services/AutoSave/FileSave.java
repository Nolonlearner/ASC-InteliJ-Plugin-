package com.github.nolonlearner.ascintelijplugin.services.AutoSave;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;

/*
    作用: 实现具体保存逻辑，将文档内容写入对应的文件。
    属性:
        Project project: 项目对象。
    构造函数:
        ActionAutoSaveStrategy(Project project): 传入项目对象。
    方法:
        void save(Document document): 保存文档。
 */
public class ActionAutoSaveStrategy {
    private final Project project;

    public ActionAutoSaveStrategy(Project project) {
        this.project = project;
    }

    public void save(Document document) {
        // 实现保存逻辑
        System.out.println("Saving document: " + document); // 示例保存逻辑
        // 实际保存实现
    }

    public Project getProject() {
        return project;
    }
}
