/*package com.github.nolonlearner.ascintelijplugin.toolWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import javax.swing.*;

public class MyToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        // 创建工具窗口的内容
        JPanel panel = new JPanel();
        panel.add(new JLabel("Hello, World!")); // 添加一个简单的标签
        toolWindow.getComponent().add(panel);
    }
}*/

package com.github.nolonlearner.ascintelijplugin.toolWindow;

import com.github.nolonlearner.ascintelijplugin.services.RandomNumberGenerator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyToolWindowFactory implements ToolWindowFactory {
    private RandomNumberGenerator randomNumberGenerator;

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        // 初始化随机数生成器
        randomNumberGenerator = new RandomNumberGenerator();

        // 创建工具窗口的内容
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout()); // 设置布局为边界布局

        JLabel randomNumberLabel = new JLabel("随机数将在这里显示");
        randomNumberLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(randomNumberLabel, BorderLayout.CENTER); // 将标签添加到面板中

        JButton generateButton = new JButton("生成随机数");
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int randomNumber = randomNumberGenerator.generateRandomNumber(100); // 生成 0 到 99 之间的随机数
                randomNumberLabel.setText("生成的随机数: " + randomNumber); // 更新标签显示随机数
            }
        });
        panel.add(generateButton, BorderLayout.SOUTH); // 将按钮添加到面板中

        toolWindow.getComponent().add(panel); // 将面板添加到工具窗口
    }
}
