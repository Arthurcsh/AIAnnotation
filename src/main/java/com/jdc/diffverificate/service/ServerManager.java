package com.jdc.diffverificate.service;

import com.alibaba.fastjson2.JSONArray;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.alibaba.fastjson2.JSONObject;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import com.jdc.diffverificate.AnnotationSettingsState;
import com.jdc.diffverificate.annotation.AnnotationInterface;
import com.jdc.diffverificate.login.LoginSuccessInterface;
import com.jdc.diffverificate.model.AntnPrompt;
import com.jdc.diffverificate.utils.MessageUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class ServerManager {

    public static void loginRequest(Project project, String username, String password, LoginSuccessInterface callback) {
        if (StringUtil.isEmpty(username) || StringUtil.isEmpty(password)) return;

        ProgressManager.getInstance().run(new RunLoginTask(project, username, password, callback));
    }

    public static void annotationRequest(Project project, String codePrompt, AnnotationInterface callback) {
        if (StringUtil.isEmpty(codePrompt)) return;

        ProgressManager.getInstance().run(new RunAnnotationTask(project, codePrompt, callback));
    }

    private static class RunLoginTask extends Task.Backgroundable {
        private Project project;
        private String username;
        private String password;
        private LoginSuccessInterface loginSuccess;

        public RunLoginTask(Project project, String name, String password, LoginSuccessInterface callback) {
            super(project, "RunLoginTask..", true);
            this.project = project;
            this.username = name;
            this.password = password;
            this.loginSuccess = callback;
        }

        @Override
        public void run(@NotNull ProgressIndicator progressIndicator) {
            terminalProgress(progressIndicator, 0);
            if (progressIndicator.isCanceled()) {
                MessageUtils.getInstance(project).showWarnMsg("提示", "Request cancelled");
                return;
            }

            // 实现相关业务逻辑
            AntnConfigPersistent config = URLUtils.getAntnPersistent(project);
            JSONObject arg = new JSONObject();
            arg.put("username", username);
            arg.put("password", password);
            System.out.println("----- Login User: "+username+"   "+password);
            AntnHttpResponse response = AntnHttpRequest.builderPost(URLUtils.getAntnLogin(), "application/json")
                    .addHeader("Accept", "application/json").body(arg.toJSONString()).request();
            String message = "";
            if (response.getStatusCode() == 200) {
                String body = response.getBody();
                System.out.println("----- Login result: "+body);
                JSONObject returnObj = JSONObject.parse(body);
                message = getLoginMessage(returnObj);
                String dataValue = returnObj.getString("data");
                if (StringUtils.isNotBlank(dataValue)) {
                    JSONObject dataObj = JSONObject.parse(dataValue);
                    String tokenValue = dataObj.getString("token");
                    URLUtils.persistentAntnOauth(project,username,password,tokenValue);
                }
                MessageUtils.getInstance(project).showInfoMsg("", "request.pending");
            } else {
                MessageUtils.getInstance(project).showWarnMsg("", "request.failed");
            }

//            MessageUtils.getInstance(project).showWarnMsg("", PropertiesUtils.getInfo("response.timeout"));
            terminalProgress(progressIndicator, 1);

            // 在状态栏提示信息【代码生成完成】
            StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
            JBPopupFactory.getInstance()
                    .createHtmlTextBalloonBuilder("登录请求已结束", MessageType.INFO, null)
                    .setFadeoutTime(7500)
                    .createBalloon()
                    .show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.atRight);
            // 弹出模态框显示以下的消息
            String finalMessage = message;
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    // 登录成功后回调
                    if (RunLoginTask.this.loginSuccess != null) {
                        try {
                            RunLoginTask.this.loginSuccess.loginCallback();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
//            ApplicationManager.getApplication().invokeLater()
            if(EventQueue.isDispatchThread()) {
                Messages.showMessageDialog(finalMessage, "提示", null);
            } else {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Messages.showMessageDialog(finalMessage, "提示", null);
                    }
                });
            }
        }
    }


    private static class RunAnnotationTask extends Task.Backgroundable {
        private Project project;
        private String prompt;
        private AntnPrompt character;
        private AnnotationInterface antnCallback;

        public RunAnnotationTask(Project project, String prompt, AnnotationInterface callback) {
            super(project, "RunAnnotationTask..", true);
            this.project = project;
            this.prompt = prompt;
            this.antnCallback = callback;
        }

        @Override
        public void run(@NotNull ProgressIndicator progressIndicator) {
            terminalProgress(progressIndicator, 0);
            if (progressIndicator.isCanceled()) {
                MessageUtils.getInstance(project).showWarnMsg("提示", "Request cancelled");
                return;
            }

            // 实现相关业务逻辑
            String arguments = annotationRequestArgument(project, prompt);
            String token = URLUtils.getOauthToken(project);
            System.out.println("----- Annotation request: "+arguments+"\nToken: "+token);

            terminalProgress(progressIndicator, 0.28f);
            AntnHttpResponse response = AntnHttpRequest.builderPost(URLUtils.getAntnCode(), "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization",token)    // 请求Token
                    .body(arguments).request();
            String message = "服务未知错误！";
            String content = "";
            terminalProgress(progressIndicator, 0.54f);

            if (response.getStatusCode() == 200) {
                String body = response.getBody();
                System.out.println("--- Annotation result: "+body);
                JSONObject returnObj = JSONObject.parse(body);
                content = returnObj.getString("data");
                message = returnObj.getString("errMsg");
//                returnObj.put("errCode", "100120");   // 模拟Token失效场景

                // Token失效后访问接口会报错100120需重新登录
                String errorCode = returnObj.getString("errCode");
                if (StringUtil.equals(errorCode,"100120")) {
                    terminalProgress(progressIndicator, 1);
                    transferInvokeLogin(antnCallback, token);
                    return;
                }
                MessageUtils.getInstance(project).showInfoMsg(""+response.getStatusCode(), "request.pending");
            } else if (response.getStatusCode() == 401) {
                terminalProgress(progressIndicator, 1);
                transferInvokeLogin(antnCallback, token);
                return;
            } else {
                message = "服务状态：" + response.getStatusCode() + "       消息: " + response.getBody();
                MessageUtils.getInstance(project).showWarnMsg("" + response.getStatusCode(), "request.failed");
            }

//            MessageUtils.getInstance(project).showWarnMsg("", PropertiesUtils.getInfo("response.timeout"));
            terminalProgress(progressIndicator, 1);

            // 在状态栏提示信息【代码生成完成】
            StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
            JBPopupFactory.getInstance()
                    .createHtmlTextBalloonBuilder("注释请求已完成 -- "+message, MessageType.INFO, null)
                    .setFadeoutTime(7500)
                    .createBalloon()
                    .show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.atRight);
            // 弹出模态框显示以下的消息
            String finalMessage = message;
            String finalContent = content;
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    // 注释成功后回调
                    if (antnCallback != null && StringUtil.isNotEmpty(finalContent)) {
                        RunAnnotationTask.this.antnCallback.annotationInvoke(finalContent);
//                        Messages.showMessageDialog(finalMessage, "提示", null);
                        MessageUtils.getInstance(project).showErrorMsg("提示",finalMessage);
                    }
                }
            });
        }
    }

    private static String annotationRequestArgument(Project project, String prompt) {
        if (StringUtil.isEmpty(prompt)) return null;

        AnnotationSettingsState settings = AnnotationSettingsState.getInstance(project);
        JSONObject argument = new JSONObject();
        JSONObject charact_lang = new JSONObject();
        JSONObject charact_code = new JSONObject();
        String gptModel = settings.getGptType()==0 ? "gpt-3.5-turbo" : "gpt-4";
        String antnLang = settings.getAnnoteLanguage()==0 ? "Chinese" : "English";
        charact_lang.put("key","language");
        charact_lang.put("value",antnLang);
        charact_code.put("key","programmingLang");
        charact_code.put("value",settings.getCodeLanguage());

        JSONArray characters = new JSONArray();
        characters.add(charact_lang);
        characters.add(charact_code);
        argument.put("model", gptModel);
        argument.put("character", characters);
        argument.put("promptText", prompt);

        return argument.toJSONString();
    }

    private static void transferInvokeLogin(AnnotationInterface antnInterface, String token) {
        if (antnInterface != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    Messages.showMessageDialog("Token失效请重新登录.", "提示", null);
                    antnInterface.annotationNeedLogin(token);
                }
            });
        }
    }

    private static void terminalProgress(ProgressIndicator progressIndicator, float progress) {
        if (progress == 0) {
            progressIndicator.setIndeterminate(true);
        } else if (progress == 1) {
            progressIndicator.setIndeterminate(false);  // 关闭进度条
            progressIndicator.setFraction(1.0);      // 设置进度为100%
            progressIndicator.setText("请求结束");
        } else {
            progressIndicator.setFraction(progress);
        }
    }

    private static String getLoginMessage(JSONObject object) {
        if (StringUtil.isEmpty(object.toJSONString())) {
            return "未知错误";
        }
        String codeKey = object.getString("errCode");
        String errorKey = object.getString("errMsg");
        if (StringUtil.isEmpty(codeKey)) {
            return "无消息";
        }
        switch (codeKey) {
            case "0":
                return "登录成功！";
            case "300008":
                return "用户名不能为空！";
            case "300009":
                return "密码不能为空！";
            case "100118":
                return "用户名或密码错误";
            case "100124":
                return "登录用户无效";
            case "100120":
                return "拒绝访问";
        }
        return errorKey;
    }


}
