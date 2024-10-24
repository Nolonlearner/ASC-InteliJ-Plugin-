package com.github.nolonlearner.ascintelijplugin.startup;
// src/main/java/com/github/nolonlearner/ascintelijplugin/startup/PluginStartupActivity.java
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;
import com.github.nolonlearner.ascintelijplugin.listeners.ListenerManager;
public class PluginStartupActivity implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        new ListenerManager(project);  // 在项目启动时初始化监听器
    }
}