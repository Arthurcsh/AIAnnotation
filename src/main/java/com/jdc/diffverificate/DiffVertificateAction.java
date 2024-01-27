package com.jdc.diffverificate;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.jdc.diffverificate.login.UserLoginDialog;

public class DiffVertificateAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        // TODO: insert action logic here
        Project project = event.getData(PlatformDataKeys.PROJECT);
//        Messages.showInputDialog(
//                project,
//                "请输入用户名&密码",
//                "用户登录",
//                Messages.getQuestionIcon());
        UserLoginDialog loginDialog = new UserLoginDialog(project);
        loginDialog.showAndGet();


//        ResponseEntity<Map> mapResponseEntity = HttpUtil.get("http://localhost:22200/getPerson");
//        System.out.println("action点击触发网络请求 = " + mapResponseEntity.getBody());


    }
}
