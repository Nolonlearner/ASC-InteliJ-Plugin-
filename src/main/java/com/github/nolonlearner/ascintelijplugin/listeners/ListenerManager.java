package com.github.nolonlearner.ascintelijplugin.listeners;
// src/main/java/com/github/nolonlearner/ascintelijplugin/listeners/ListenerManager.java
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;

import java.util.HashMap;
import java.util.Map;

/*
    * 监听器管理器
    * EditorFactoryListener 是 IntelliJ 平台中与编辑器相关的事件类，主要用于在编辑器的创建和删除过程中传递信息。
    * 这个类在 EditorFactoryListener 接口的实现中使用，用于处理编辑器的生命周期事件。
 */
public class ListenerManager implements EditorFactoryListener {
    private final Project project;
    private final Map<VirtualFile, DocumentListener> documentListenerMap;
    private final DocVirtualListener fileListenerManager;

    public ListenerManager(Project project) {
        System.out.println("ListenerManager 构造函数");
        this.project = project;
        documentListenerMap = new HashMap<>();
        fileListenerManager = new DocVirtualListener();

        // 注册 DocVirtualListener
        VirtualFileManager.getInstance().addVirtualFileListener(fileListenerManager);

        // 注册 EditorFactoryListener
        EditorFactory.getInstance().addEditorFactoryListener(this, project);
    }

    @Override
    public void editorCreated(EditorFactoryEvent event) {
        // 获取当前编辑器的文档
        Document document = event.getEditor().getDocument();
        VirtualFile currentFile = getCurrentOpenFile();

        System.out.println("editorCreated document: " + document);
        System.out.println("editorCreated currentFile: " +currentFile);


        if (currentFile != null && !documentListenerMap.containsKey(currentFile)) {
            // 创建并注册 DocumentListener
            DocListener docListener = new DocListener(document);
            document.addDocumentListener(docListener);
            documentListenerMap.put(currentFile, docListener);
        }

        System.out.println("编辑器创建，当前文件: " + currentFile.getPath());
        // 此处有bug，创建一个编辑器后，再打开别的编辑器，会两次触发相同的 editorCreated 事件
        // 比如创建Main.java后，再打开Test.java，会触发两次 editorCreated 事件
        // 输出的是：编辑器创建，当前文件: C:/Users/何如麟/IdeaProjects/test/src/Main.java
        //         编辑器创建，当前文件: C:/Users/何如麟/IdeaProjects/test/src/Main.java
        // Main.java 输出了两次，这是因为 EditorFactoryListener 是全局的，不是针对某个文件的
        // 但是 DocListener 是针对某个文件的，所以会有这个问题，要怎么解决呢？
    }

    public void editorRemoved(EditorFactoryEvent event) {
        // 获取当前编辑器的文档
        Document document = event.getEditor().getDocument();
        VirtualFile currentFile = getCurrentOpenFile();

        if (currentFile != null) {
            // 移除 DocumentListener
            DocumentListener listener = documentListenerMap.remove(currentFile);
            if (listener != null) {
                document.removeDocumentListener(listener);
            }
            System.out.println("编辑器移除，当前文件: " + currentFile.getPath());
        }
    }

    private VirtualFile getCurrentOpenFile() {
        VirtualFile[] files = FileEditorManager.getInstance(project).getOpenFiles();
        return files.length > 0 ? files[0] : null; // 返回第一个打开的文件
    }
}
