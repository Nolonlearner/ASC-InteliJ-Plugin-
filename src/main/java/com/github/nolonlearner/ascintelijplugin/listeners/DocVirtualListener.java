package com.github.nolonlearner.ascintelijplugin.listeners;
// src/main/java/com/github/nolonlearner/ascintelijplugin/listeners/DocVirtualListener.java

import com.intellij.openapi.vfs.*;
import com.intellij.openapi.vfs.VirtualFileEvent;
import org.jetbrains.annotations.NotNull;
public class DocVirtualListener implements VirtualFileListener {

    @Override
    public void propertyChanged(@NotNull VirtualFilePropertyEvent event) {// 属性改变
        System.out.println("属性改变");
    }

    @Override
    public void contentsChanged(@NotNull VirtualFileEvent event) {// 内容改变
        System.out.println("内容改变");
        System.out.println(event.getFile().getPath());
        System.out.println(event.getFile().getName());
        System.out.println(event.getFile().getFileType());
    }

    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {// 文件创建
        System.out.println("文件创建");
        System.out.println(event.getFile().getPath());
        System.out.println(event.getFile().getName());
        System.out.println(event.getFile().getFileType());
    }

    @Override
    public void fileDeleted(@NotNull VirtualFileEvent event) {// 文件删除
        System.out.println("文件删除");
        System.out.println(event.getFile().getPath());
        System.out.println(event.getFile().getName());
        System.out.println(event.getFile().getFileType());
    }

    @Override
    public void fileMoved(@NotNull VirtualFileMoveEvent event) {// 文件移动
        System.out.println("文件移动");
    }

    @Override
    public void fileCopied(@NotNull VirtualFileCopyEvent event) {// 文件复制
        System.out.println("文件复制");
    }

    @Override
    public void beforePropertyChange(@NotNull VirtualFilePropertyEvent event) {// 属性改变之前
        System.out.println("属性改变之前");
    }

    @Override
    public void beforeContentsChange(@NotNull VirtualFileEvent event) {// 内容改变之前
        System.out.println("内容改变之前");
    }

    @Override
    public void beforeFileDeletion(@NotNull VirtualFileEvent event) {// 文件删除之前
        System.out.println("文件删除之前");
    }

    @Override
    public void beforeFileMovement(@NotNull VirtualFileMoveEvent event) {// 文件移动之前
        System.out.println("文件移动之前");
    }
}
