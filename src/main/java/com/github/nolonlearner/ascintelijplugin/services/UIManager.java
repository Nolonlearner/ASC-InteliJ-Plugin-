package com.github.nolonlearner.ascintelijplugin.services;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.ui.Messages;

import java.util.LinkedList;
import java.util.List;

public class UIManager {
    private VersionManager versionManager;

    public UIManager(VersionManager versionManager) {
        this.versionManager = versionManager;
    }

    // 展示版本历史
    public void showVersionHistory(String filePath) {
        LinkedList<VersionRecord> versions = versionManager.getVersions(filePath);
        StringBuilder history = new StringBuilder();
        for (VersionRecord version : versions) {
            history.append("Version ID: ").append(version.getVersionId())
                    .append(", Timestamp: ").append(version.getTimestamp())
                    .append("\n");
        }
        Messages.showInfoMessage(history.toString(), "Version History");
    }

    /*
    // 展示差异
    public void showDiff(String filePath, List<String> original, List<String> revised) {
        List<String> diff = DiffAlgorithm.computeDiff(original, revised);
        StringBuilder diffString = new StringBuilder();
        for (String line : diff) {
            diffString.append(line).append("\n");
        }
        Messages.showInfoMessage(diffString.toString(), "Diff");
    }
    */

}
