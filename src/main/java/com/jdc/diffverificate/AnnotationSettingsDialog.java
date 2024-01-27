package com.jdc.diffverificate;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.jdc.diffverificate.login.UserLoginDialog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.ui.ComboBox;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;


public class AnnotationSettingsDialog extends DialogWrapper {
    private final Project project;
    public AnnotationSettingsDialog(Project project) {
        super(true);
        this.project = project;
        setTitle("代码注释设置");
        setSize(460, 380);
        setResizable(false);
        init();

        initSettings();

/**
 * Ai新增的代码注释信息，
 * 获取资源图片，异常捕获....
 */
        try {
            ImageIcon icon = new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("images/icon_annotation.png")));
            getWindow().setIconImage(icon.getImage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        ImageIcon icon = new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("images/icon_annotation.png")));
//        getPeer().getWindow().setIconImage(icon.getImage());
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel gptLabel = new JLabel("Ai-GPT");
        gptLabel.setBounds(64,20,80,25);
        panel.add(gptLabel);
        ComboBox<String> gptComboBox = new ComboBox<>();
        gptComboBox.setBounds(64,46,135,35);
        panel.add(gptComboBox);
        gptComboBox.addItem("GPT-3");
        gptComboBox.addItem("GPT-4");
        gptComboBox.setSelectedIndex(1);

        JLabel langLabel = new JLabel("注释语言");
        langLabel.setBounds(64,96,80,25);
        panel.add(langLabel);
        ComboBox<String> langComboBox = new ComboBox<>();
        langComboBox.setBounds(64,122,135,35);
        panel.add(langComboBox);
        langComboBox.addItem("中文");
        langComboBox.addItem("英文");
        langComboBox.setSelectedIndex(1);

        JLabel codeLabel = new JLabel("编码语言");
        codeLabel.setBounds(266,20,80,25);
        panel.add(codeLabel);
        JCheckBox codeCheckBox = new JCheckBox();
        codeCheckBox.setBounds(266,46,25,25);
        panel.add(codeCheckBox);
        JLabel javaLabel = new JLabel("Java");
        javaLabel.setBounds(292,46,80,25);
        panel.add(javaLabel);

        JCheckBox cplusCheckBox = new JCheckBox();
        cplusCheckBox.setBounds(266,76,25,25);
        panel.add(cplusCheckBox);
        JLabel cplusLabel = new JLabel("C++");
        cplusLabel.setBounds(292,76,80,25);
        panel.add(cplusLabel);

        JCheckBox pythonCheckBox = new JCheckBox();
        pythonCheckBox.setBounds(266,106,25,25);
        panel.add(pythonCheckBox);
        JLabel pythonLabel = new JLabel("Python");
        pythonLabel.setBounds(292,106,80,25);
        panel.add(pythonLabel);

        JCheckBox cssCheckBox = new JCheckBox();
        cssCheckBox.setBounds(266,136,25,25);
        panel.add(cssCheckBox);
        JLabel cssLabel = new JLabel("CSS");
        cssLabel.setBounds(292,136,80,25);
        panel.add(cssLabel);

        JCheckBox tsCheckBox = new JCheckBox();
        tsCheckBox.setBounds(266,166,25,25);
        panel.add(tsCheckBox);
        JLabel tsLabel = new JLabel("TypeScript");
        tsLabel.setBounds(292,166,80,25);
        panel.add(tsLabel);

        JCheckBox ocCheckBox = new JCheckBox();
        ocCheckBox.setBounds(266,196,25,25);
        panel.add(ocCheckBox);
        JLabel ocLabel = new JLabel("Objective-C");
        ocLabel.setBounds(292,196,80,25);
        panel.add(ocLabel);

        JCheckBox swiftCheckBox = new JCheckBox();
        swiftCheckBox.setBounds(266,226,25,25);
        panel.add(swiftCheckBox);
        JLabel swiftLabel = new JLabel("Swift");
        swiftLabel.setBounds(292,226,80,25);
        panel.add(swiftLabel);

        JCheckBox allCheckBox = new JCheckBox();
        allCheckBox.setBounds(266,256,25,25);
        panel.add(allCheckBox);
        JLabel alltLabel = new JLabel("全部");
        alltLabel.setBounds(292,256,80,25);
        panel.add(alltLabel);

        // java c++ python css typescript objective-c swift
        return panel;
    }

    private void initSettings() {
        getButton(getCancelAction()).setText("取消");
        getButton(getOKAction()).setText("上传");

        uploadProgress();
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        UserLoginDialog loginDialog = new UserLoginDialog(project);
        loginDialog.showAndGet();

    }

    private void uploadProgress() {
//        ProgressManager progressManager = ProgressManager.getInstance();
//        final ProgressIndicator progressIndicator = progressManager.getProgressIndicator();
//        progressIndicator.setBounds(266,256,25,25);
//        panel.add(progressIndicator);
//        PanelProgressIndicator indicator;

        ProgressManager.getInstance().run(new Task.Backgroundable(project, "TitleKKKKK"){
            public void run(@NotNull ProgressIndicator progressIndicator) {

                // start your process
                // Set the progress bar percentage and text
                progressIndicator.setFraction(0.10);
                progressIndicator.setText("90% to finish");

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                // 28% done
                progressIndicator.setFraction(0.28);
                progressIndicator.setText("50% to finish");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                // 62% done
                progressIndicator.setFraction(0.62);
                progressIndicator.setText("50% to finish");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                // 87% done
                progressIndicator.setFraction(0.87);
                progressIndicator.setText("50% to finish");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                // Finished
                progressIndicator.setFraction(1.0);
                progressIndicator.setText("finished");

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }});
    }
}



