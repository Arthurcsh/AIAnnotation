package com.jdc.diffverificate;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.jdc.diffverificate.annotation.AnnotationInterface;
import com.jdc.diffverificate.login.AnnotationLoginInterface;
import com.jdc.diffverificate.login.UserLoginDialog;
import com.jdc.diffverificate.service.ServerManager;
import org.jetbrains.annotations.NotNull;

public class DiffAnnotationAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {

        // 获取到idea编辑界面实例
        selectedCurrentAnnotation(event);

    }

    /**
     * 注意update方法决定是否显示菜单
     * @param e Carries information on the invocation place and data available
     */
    @Override
    public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        PsiElement psiElement = e.getData(PlatformDataKeys.PSI_ELEMENT);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (psiElement != null && editor != null && psiElement.getNode() != null) {
            IElementType elementType = psiElement.getNode().getElementType();
            // 如果光标所在位置是一个方法，则鼠标右键的时候，显示菜单
            presentation.setEnabledAndVisible(true);
        } else {
            presentation.setEnabledAndVisible(false);
        }
    }

    private void selectedCurrentAnnotation(AnActionEvent event) {
        final Project project = event.getRequiredData(CommonDataKeys.PROJECT);
        Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
        // 获取编辑实例选择模式
        SelectionModel selectionModel = editor.getSelectionModel();
        // 获取选中文本信息
        String selectedText = selectionModel.getSelectedText();
        // 设置数据中心数据
//        DataCenter.SELECT_CODE = selectedText;
        System.out.println(":::: Select Code: "+selectedText);

        performAnnotation(project, event, selectedText);
    }

    private void performAnnotation(Project project, AnActionEvent event, String prompts) {
        if (StringUtil.isEmpty(prompts)) { return; }

        ServerManager.annotationRequest(project, prompts, new AnnotationInterface() {
            @Override
            public void annotationInvoke(String annotation) {
                System.out.println("--- Annotation Code: "+annotation);
                rewriteAnnotation(event, annotation);
            }

            @Override
            public void annotationNeedLogin(String token) {
                UserLoginDialog loginDialog = new UserLoginDialog(project);
                loginDialog.setLoginInterface(new AnnotationLoginInterface() {
                    @Override
                    public void invokeLogin() throws InterruptedException {
                        Thread.sleep(300L);
                        // 登录成功后重新请求注释
                        selectedCurrentAnnotation(event);
                    }
                });
                loginDialog.showAndGet();
            }
        });
    }

    /**
     * 从AI返回后重写该方法的注释
     * @param event
     * @param annotation
     */
    private void rewriteAnnotation(AnActionEvent event, String annotation) {
        if (StringUtil.isEmpty(annotation)) { return; }

        final Project project = event.getRequiredData(CommonDataKeys.PROJECT);
        Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
        final Document document = editor.getDocument();

        // 获取选择信息，Caret是一种文本表示方法
        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
        int start = primaryCaret.getSelectionStart();
        int end = primaryCaret.getSelectionEnd();

        // 替换鼠标选择的文本内容为editor_basics
        new Thread(new Runnable(){
            @Override
            public void run() {
                WriteCommandAction.runWriteCommandAction(project, () ->
                        document.replaceString(start, end, annotation)
                );
            }
        }).start();
        // 移除选择操作
        primaryCaret.removeSelection();
    }

}

