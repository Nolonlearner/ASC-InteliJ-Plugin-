package com.github.nolonlearner.ascintelijplugin.services;

import java.util.List;

public class RollbackManager {
    private VersionManager versionManager;

    public RollbackManager(VersionManager versionManager) {
        this.versionManager = versionManager;
    }

    // 回滚到最新版本
    public void rollbackToLatest(String filePath) {
        VersionRecord latestVersion = versionManager.getLatestVersion(filePath);
        if (latestVersion != null) {
            applyChanges(latestVersion.getChanges());
        }
    }

    // 应用变更
    private void applyChanges(List<Change> changes) {
        for (Change change : changes) {
            // 处理每个变更的应用逻辑，具体实现可根据项目需求定义
            System.out.println("Applying change: " + change.getChangeType() + " - " + change.getContent());
        }
    }
}
