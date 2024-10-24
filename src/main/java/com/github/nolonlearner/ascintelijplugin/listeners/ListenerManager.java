package com.github.nolonlearner.ascintelijplugin.listeners;
// src/main/java/com/github/nolonlearner/ascintelijplugin/listeners/ListenerManager.java
import com.github.nolonlearner.ascintelijplugin.services.AutoSave.AutoSaveManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
    * 监听器管理器
    * EditorFactoryListener 是 IntelliJ 平台中与编辑器相关的事件类，主要用于在编辑器的创建和删除过程中传递信息。
    * 这个类在 EditorFactoryListener 接口的实现中使用，用于处理编辑器的生命周期事件。
 */
public class ListenerManager implements EditorFactoryListener {
    private final Project project;// 项目实例
    private final Map<VirtualFile, DocumentListener> documentListenerMap;// 文档监听器映射
    private final Map<VirtualFile, List<Object>> testlistenerMap;
    private final DocVirtualListener docVirtualListener;// 文档虚拟监听器

    public ListenerManager(Project project) {
        System.out.println("进入 ListenerManager 构造函数");
        this.project = project;
        documentListenerMap = new HashMap<>();
        docVirtualListener = new DocVirtualListener();
        testlistenerMap = new HashMap<>();

        // 注册 DocVirtualListener
        VirtualFileManager.getInstance().addVirtualFileListener(docVirtualListener);

        // 注册 EditorFactoryListener
        EditorFactory.getInstance().addEditorFactoryListener(this, project);

        // 因为打开IDEA的时候会有默认的editor，但并没有绑定监听器，所以需要手动检查已经打开的编辑器，并为其绑定 DocumentListener
        bindToAlreadyOpenedEditors();
    }

    @Override
    public void editorCreated(EditorFactoryEvent event) {
        System.out.println("尝试创建编辑器");
        Document document = event.getEditor().getDocument();
        VirtualFile currentFile = FileDocumentManager.getInstance().getFile(document);

        if (currentFile != null) {
            // 确保 testlistenerMap 中有一个列表
            testlistenerMap.putIfAbsent(currentFile, new ArrayList<>());
            List<Object> listeners = testlistenerMap.get(currentFile);

            // 创建并注册 ActionListener
            AutoSaveManager autoSaveManager = new AutoSaveManager(project);

            ActionListener actionListener = new ActionListener(document, autoSaveManager);
            document.addDocumentListener(actionListener);
            listeners.add(actionListener);

            TimeListener timeListener = new TimeListener(document, autoSaveManager);
            document.addDocumentListener(timeListener);
            listeners.add(timeListener);

            StructureListener structureListener = new StructureListener(document, project, autoSaveManager);
            document.addDocumentListener(structureListener);
            listeners.add(structureListener);

            System.out.println("Registered listeners for file: " + currentFile.getPath());
        }
        System.out.println("编辑器创建，当前文件: " + currentFile.getPath());
    }

    @Override
    public void editorReleased(EditorFactoryEvent event) {
        // 获取当前编辑器的文档
        System.out.println("触发编辑器移除");
        Document document = event.getEditor().getDocument();
        VirtualFile currentFile = FileDocumentManager.getInstance().getFile(document);
        System.out.println("editorRemoved document: " + document);
        System.out.println("editorRemoved currentFile: " +currentFile);
        if (currentFile != null) {
            // 移除监听器
            List<Object> testlistener = testlistenerMap.remove(currentFile); // 移除监听器

            if (testlistener != null) {
                for (Object listener : testlistener) {
                    if (listener instanceof DocumentListener) { // 确保是 DocumentListener
                        document.removeDocumentListener((DocumentListener) listener); // 移除监听器
                        System.out.println("Removed listener: " + listener.getClass());
                    }
                }
            }
            System.out.println("test编辑器移除，当前文件: " + currentFile.getPath());
        }

    }

    private VirtualFile getCurrentOpenFile() {
        VirtualFile[] files = FileEditorManager.getInstance(project).getOpenFiles();
        return files.length > 0 ? files[0] : null; // 返回第一个打开的文件
    }

    // 绑定已经打开的编辑器
    private void bindToAlreadyOpenedEditors(){
        System.out.println("test尝试绑定已经打开的编辑器");
        VirtualFile[] openFiles = FileEditorManager.getInstance(project).getOpenFiles();
        for (VirtualFile file : openFiles) {
            System.out.println("已打开的文件: " + file.getPath());
            Document document = FileDocumentManager.getInstance().getDocument(file);
            if (document != null) {
                // 确保 testlistenerMap 中有一个列表
                testlistenerMap.putIfAbsent(file, new ArrayList<>());
                List<Object> listeners = testlistenerMap.get(file);

                // 创建并注册 ActionListener
                AutoSaveManager autoSaveManager = new AutoSaveManager(project);

                ActionListener actionListener = new ActionListener(document, autoSaveManager);
                document.addDocumentListener(actionListener);
                listeners.add(actionListener);

                TimeListener timeListener = new TimeListener(document, autoSaveManager);
                document.addDocumentListener(timeListener);
                listeners.add(timeListener);

                StructureListener structureListener = new StructureListener(document, project, autoSaveManager);
                document.addDocumentListener(structureListener);
                listeners.add(structureListener);

                // No need to put it back, as we already initialized it above
                System.out.println("test已恢复编辑器绑定，文件: " + file.getPath());
            }
        }
    }
}
