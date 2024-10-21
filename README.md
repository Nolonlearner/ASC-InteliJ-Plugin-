# ASC-InteliJ-Plugin-

![Build](https://github.com/Nolonlearner/ASC-InteliJ-Plugin-/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)

## Template ToDo list
- [x] Create a new [IntelliJ Platform Plugin Template][template] project.
- [ ] Get familiar with the [template documentation][template].
- [ ] Adjust the [pluginGroup](./gradle.properties) and [pluginName](./gradle.properties), as well as the [id](./src/main/resources/META-INF/plugin.xml) and [sources package](./src/main/kotlin).
- [ ] Adjust the plugin description in `README` (see [Tips][docs:plugin-description])
- [ ] Review the [Legal Agreements](https://plugins.jetbrains.com/docs/marketplace/legal-agreements.html?from=IJPluginTemplate).
- [ ] [Publish a plugin manually](https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html?from=IJPluginTemplate) for the first time.
- [ ] Set the `MARKETPLACE_ID` in the above README badges. You can obtain it once the plugin is published to JetBrains Marketplace.
- [ ] Set the [Plugin Signing](https://plugins.jetbrains.com/docs/intellij/plugin-signing.html?from=IJPluginTemplate) related [secrets](https://github.com/JetBrains/intellij-platform-plugin-template#environment-variables).
- [ ] Set the [Deployment Token](https://plugins.jetbrains.com/docs/marketplace/plugin-upload.html?from=IJPluginTemplate).
- [ ] Click the <kbd>Watch</kbd> button on the top of the [IntelliJ Platform Plugin Template][template] to be notified about releases containing new features and fixes.

<!-- Plugin description -->
This Fancy IntelliJ Platform Plugin is going to be your implementation of the brilliant ideas that you have.

This specific section is a source for the [plugin.xml](/src/main/resources/META-INF/plugin.xml) file which will be extracted by the [Gradle](/build.gradle.kts) during the build process.

To keep everything working, do not remove `<!-- ... -->` sections. 
<!-- Plugin description end -->

## Installation

- Using the IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "ASC-InteliJ-Plugin-"</kbd> >
  <kbd>Install</kbd>
  
- Using JetBrains Marketplace:

  Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID) and install it by clicking the <kbd>Install to ...</kbd> button in case your IDE is running.

  You can also download the [latest release](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID/versions) from JetBrains Marketplace and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

- Manually:

  Download the [latest release](https://github.com/Nolonlearner/ASC-InteliJ-Plugin-/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation


# Listeners 文件夹说明

此文件夹包含 IntelliJ IDEA 插件中的监听器类，负责监控文档和文件的变化。以下是各个类的简要介绍。

## 1. DocListener

- **路径**: `com.github.nolonlearner.ascintelijplugin.listeners.DocListener`
- **功能**: 监控文档内容的变化。
- **主要方法**:
  - `beforeDocumentChange(DocumentEvent event)`: 记录旧文本。
  - `documentChanged(DocumentEvent event)`: 记录新文本并打印变化信息。

## 2. DocVirtualListener

- **路径**: `com.github.nolonlearner.ascintelijplugin.listeners.DocVirtualListener`
- **功能**: 监控虚拟文件的变化。
- **主要方法**:
  - `fileCreated(VirtualFileEvent event)`: 处理文件创建。
  - `fileDeleted(VirtualFileEvent event)`: 处理文件删除。
  - `contentsChanged(VirtualFileEvent event)`: 处理文件内容改变。

## 3. ListenerManager

- **路径**: `com.github.nolonlearner.ascintelijplugin.listeners.ListenerManager`
- **功能**: 管理文档和虚拟文件的监听器。
- **主要方法**:
  - `editorCreated(EditorFactoryEvent event)`: 注册 `DocListener`。
  - `editorReleased(EditorFactoryEvent event)`: 移除 `DocListener`。
  - `bindToAlreadyOpenedEditors()`: 绑定已打开的编辑器。

## 4. 概念解释

- **Document**: 文本文件内容的表示，支持修改和撤销。
- **VirtualFile**: 对文件系统的抽象表示。
- **FileDocumentManager**: 管理 `Document` 和 `VirtualFile` 的关系。
- **DocumentListener**: 监听文档内容变化的接口。
- **VirtualFileListener**: 监听虚拟文件变化的接口。

## 5. 使用说明

在项目启动时创建 `ListenerManager` 实例来初始化和注册所有监听器。

```java
public class MyStartupActivity implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        new ListenerManager(project);  // 初始化监听器
    }
}

