package com.github.nolonlearner.ascintelijplugin.services.AutoSave;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;

/*
    作用: 实现具体保存逻辑，将文档内容写入对应的文件。
    属性:
        Project project: 项目对象。
    构造函数:
        FileSave(Project project): 传入项目对象。
    方法:
        void save(Document document): 保存文档。
 */
public class FileSave {
    private final Project project;
    private String filePath = "src/main/resources/test/"; // 文件保存路径

    public FileSave(Project project) {
        this.project = project;
    }
    public FileSave(Project project, String filePath) {
        this.project = project;
        this.filePath = filePath;
    }

    public void save(Document document) {
        // 实现保存逻辑
        System.out.println("Saving document: " + document); // 示例保存逻辑
        // 实际保存实现
    }

    public Project getProject() {
        return project;
    }

    public String getFilePath() {
        return filePath;
    }
}
