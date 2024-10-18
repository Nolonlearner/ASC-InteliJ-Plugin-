package com.github.nolonlearner.ascintelijplugin.listeners;

import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.wm.IdeFrame;

public class MyApplicationActivationListener implements ApplicationActivationListener {
    private static final Logger LOG = Logger.getInstance(MyApplicationActivationListener.class);

    @Override
    public void applicationActivated(IdeFrame ideFrame) {
        LOG.warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.");
    }

    @Override
    public void applicationDeactivated(IdeFrame ideFrame) {
        // 如果你需要在应用停用时添加逻辑，可以在这里实现
        LOG.info("Application Deactivated");
    }
}

/*
package com.github.nolonlearner.ascintelijplugin.listeners

import com.intellij.openapi.application.ApplicationActivationListener
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.wm.IdeFrame

internal class MyApplicationActivationListener : ApplicationActivationListener {

    override fun applicationActivated(ideFrame: IdeFrame) {
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }
}

 */