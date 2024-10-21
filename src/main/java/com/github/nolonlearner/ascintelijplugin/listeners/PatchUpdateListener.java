package com.github.nolonlearner.ascintelijplugin.listeners;

import com.github.difflib.patch.Patch;

/*
    * 这是一个回调接口，用于监听补丁更新事件
    * 当补丁更新时，会触发这个接口的回调方法
 */
public interface PatchUpdateListener {
    void onPatchUpdated(Patch<String> patch);
}
