package com.github.nolonlearner.ascintelijplugin.services;

import com.intellij.openapi.project.Project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GitManager {

    // 创建细粒度分支并提交修改
    public void createFineGrainedBranch(Project project) {
        String branchName = "fine-grained-branch";
        runGitCommand("git checkout -b " + branchName, project);
        System.out.println("New fine-grained branch created.");
    }

    // 运行Git命令
    private void runGitCommand(String command, Project project) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", command);
        processBuilder.directory(new java.io.File(project.getBasePath())); // 指定项目目录

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Git command executed successfully.");
            } else {
                System.err.println("Error executing Git command.");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
