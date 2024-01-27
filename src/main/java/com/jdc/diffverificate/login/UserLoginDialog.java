package com.jdc.diffverificate.login;

import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.ui.ComponentValidator;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.jdc.diffverificate.service.ServerManager;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.ide.util.PropertiesComponent;
import javax.swing.*;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.EventListener;
import java.util.Objects;

public class UserLoginDialog extends DialogWrapper {
    private final Project project;

    private  final JTextField userText = new JTextField(20);
    private final JPasswordField passwordText = new JPasswordField(20);
    private static final String USER_MESSAGE = "The user name should not be empty.";
    private static final String PASS_MESSAGE = "The password should not be empty.";
    private AnnotationLoginInterface loginInterface;
    public void setLoginInterface(AnnotationLoginInterface loginCallback) {
        this.loginInterface = loginCallback;
    }

    public UserLoginDialog(Project project) {
        super(true);
        this.project = project;
        setTitle("授权登录");
        setResizable(false);
        setSize(360, 200);
        init();

        initLoginAction();
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
        final JPanel panel = new JPanel();
        //缓存记录
        PropertiesComponent cache = PropertiesComponent.getInstance(project);
//        PropertiesComponent.getInstance().setValue("key","valuw");
        panel.setLayout(null);
        // 创建 JLabel
        JLabel userLabel = new JLabel("帐号");
        /* 这个方法定义了组件的位置。
         */
        userLabel.setBounds(60,30,40,25);
        panel.add(userLabel);

        /*
         * 创建文本域用于用户输入
         */
        userText.setBounds(90,24,185,35);
        userText.setText(cache.getValue("userName"));
        panel.add(userText);

        // 输入密码的文本域
        JLabel passwordLabel = new JLabel("密码");
        passwordLabel.setBounds(60,64,40,25);
        panel.add(passwordLabel);

        passwordText.setBounds(90,60,185,35);
        passwordText.setText(cache.getValue("password"));
        panel.add(passwordText);

        JLabel lb = new JLabel();
//        ImageIcon icon = new ImageIcon(getClass().getResource("/icon_annotation.png"));
        try {
            ImageIcon icon = new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("images/icon_annotation.png")));
            icon.setImage(icon.getImage().getScaledInstance(10, 10, Image.SCALE_DEFAULT));
            lb.setIcon(icon);
            lb.setBounds(46, 36, 12, 12);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        panel.add(lb);

        return panel;
    }

    private void initLoginAction() {
        setCancelButtonText("取消");
        setOKButtonText("确定");

        userText.setText("dev@zy.com");
        passwordText.setText("123456");
//        configSecurity();

        new ComponentValidator(getDisposable()).withValidator(() -> {
            String tt = userText.getText();
            return (StringUtil.isNotEmpty(tt))? null : (new ValidationInfo(USER_MESSAGE, userText).withOKEnabled());
        }).withFocusValidator(() -> {
            String tt = userText.getText();
            return (StringUtil.isNotEmpty(tt))? null : (new ValidationInfo(USER_MESSAGE, userText).withOKEnabled());
        }).andRegisterOnDocumentListener(userText).installOn(userText);


        new ComponentValidator(getDisposable()).withValidator(() -> {
            String tt = passwordText.getText();
            return (StringUtil.isNotEmpty(tt))? null : (new ValidationInfo(PASS_MESSAGE, passwordText).withOKEnabled());
        }).withFocusValidator(() -> {
            String tt = passwordText.getText();
            return (StringUtil.isNotEmpty(tt))? null : (new ValidationInfo(PASS_MESSAGE, passwordText).withOKEnabled());
        }).andRegisterOnDocumentListener(passwordText).installOn(passwordText);

//        new ValidationInfo("The host cannot be reached", userText).withOkEnabled();
    }


    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return userText;
    }

    /**
     * 确认“OK“按钮事件
     */
    @Override
    protected void doOKAction() {
        String username = userText.getText();
        String password = passwordText.getText();
        boolean disableOK = StringUtil.isEmpty(username) || StringUtil.isEmpty(password);
        if (disableOK) return;

        ServerManager.loginRequest(project, username, password, new LoginSuccessInterface() {
            @Override
            public void loginCallback() throws InterruptedException {
                UserLoginDialog.super.doOKAction();
                if (ObjectUtils.isNotEmpty(loginInterface)) {
                    loginInterface.invokeLogin();
                }
            }
        });

//        if (loginInterface != null) {
//            loginInterface.invokeLogin();
//        }
    }

    private void configSecurity() {
        JButton viewBtn = new JButton(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("images/icon_visiable.png"))));
        JButton viewHideBtn = new JButton(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("images/icon_invisiable.png"))));
        passwordText.putClientProperty("JTextField.trailingComponent", viewBtn);   //给显示密码图标绑定单击事件
        viewBtn.addActionListener(new ActionListener() {
            //给隐藏密码图标绑定单击事件
            public void actionPerformed(ActionEvent e) {
                passwordText.putClientProperty("JTextField.trailingComponent", viewHideBtn);
            }
        });
        viewHideBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                passwordText.putClientProperty("JTextField.trailingComponent", viewBtn);
            }
        });
    }

}


