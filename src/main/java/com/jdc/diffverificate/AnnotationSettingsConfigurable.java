package com.jdc.diffverificate;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Iterator;


public class AnnotationSettingsConfigurable implements Configurable {
    private JComponent settingsComponent;

    private ComboBox<String> gptComboBox;
    private ComboBox<String> langComboBox;
    private ButtonGroup checkboxGroup = new ButtonGroup();

    private Project project;

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Annotation Settings(AI)";
    }

    public AnnotationSettingsConfigurable(Project project) {
        this.project = project;
    }

    @Override
    public @Nullable JComponent createComponent() {
        settingsComponent = configurableComponent();

        return settingsComponent;
    }

    private JComponent configurableComponent() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        int axis_x = -8;
        int axis_y = -4;
        JLabel gptLabel = new JLabel("GPT版本");
        gptLabel.setBounds(axis_x + 24, axis_y + 20, 80, 25);
        panel.add(gptLabel);
        gptComboBox = new ComboBox<>();
        gptComboBox.setBounds(axis_x + 24, axis_y + 46, 135, 35);
        panel.add(gptComboBox);
        gptComboBox.addItem("GPT-3");
        gptComboBox.addItem("GPT-4");
        gptComboBox.setSelectedIndex(1);

        JLabel langLabel = new JLabel("注释语言");
        langLabel.setBounds(axis_x + 24, axis_y + 96, 80, 25);
        panel.add(langLabel);
        langComboBox = new ComboBox<>();
        langComboBox.setBounds(axis_x + 24, axis_y + 122, 135, 35);
        panel.add(langComboBox);
        langComboBox.addItem("中文");
        langComboBox.addItem("英文");
        langComboBox.setSelectedIndex(1);

        JLabel codeLabel = new JLabel("编码语言");
        codeLabel.setBounds(axis_x + 216, axis_y + 20, 80, 25);
        panel.add(codeLabel);

        addCodeLanguageComponent(panel,"Java", "lang_java",axis_x + 216, axis_y + 46);
        addCodeLanguageComponent(panel,"C++", "lang_cplus",axis_x + 216, axis_y + 76);
        addCodeLanguageComponent(panel,"Python", "lang_python",axis_x + 216, axis_y + 106);
        addCodeLanguageComponent(panel,"JavaScript", "lang_js",axis_x + 216, axis_y + 136);
        addCodeLanguageComponent(panel,"CSS", "lang_css",axis_x + 216, axis_y + 166);
        addCodeLanguageComponent(panel,"TypeScript", "lang_typescript",axis_x + 216, axis_y + 196);
        addCodeLanguageComponent(panel,"Objective-C", "lang_objc",axis_x + 216, axis_y + 226);
        addCodeLanguageComponent(panel,"Swift", "lang_swift",axis_x + 216, axis_y + 256);

        return panel;
    }

    private void addCodeLanguageComponent(JPanel container, String title, String name, int locX, int locY) {
        JCheckBox checkBox = new JCheckBox(title);
        checkBox.setIconTextGap(8);
        checkBox.setName(name);
        checkBox.setBounds(locX, locY, 125, 25);
        container.add(checkBox, checkboxGroup);
        checkboxGroup.add(checkBox);
    }


    /**
     * 设置apply按钮是否可用，数据修改时被调用
     *
     * @return
     */
    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        AnnotationSettingsState settings = AnnotationSettingsState.getInstance(project);
        settings.setGptType(gptComboBox.getSelectedIndex());
        settings.setAnnoteLanguage(langComboBox.getSelectedIndex());
//        JCheckBox selectedCode = (JCheckBox) checkboxGroup.getSelection();
        Iterator<AbstractButton> iterator = checkboxGroup.getElements().asIterator();
        while (iterator.hasNext()) {
            JCheckBox checkBox = (JCheckBox) iterator.next();
            if (checkBox.isSelected()) {
                settings.setCodeLanguage(checkBox.getName());
                System.out.println("--------- apply: " + checkBox.getName());
                break;
            }
        }

    }

    /**
     * reset按钮被点击时触发
     */
    @Override
    public void reset() {
        AnnotationSettingsState settings = AnnotationSettingsState.getInstance(project);
        gptComboBox.setSelectedIndex(settings.getGptType());
        langComboBox.setSelectedIndex(settings.getAnnoteLanguage());

        String codeLanguage = settings.getCodeLanguage();
        System.out.println("--------- reset: " + codeLanguage);

        Iterator<AbstractButton> iterator = checkboxGroup.getElements().asIterator();
        while (iterator.hasNext()) {
            JCheckBox checkBox = (JCheckBox) iterator.next();
            checkBox.setSelected(StringUtil.equals(codeLanguage, checkBox.getName()));
        }

    }

    @Override
    public void disposeUIResources() {
        settingsComponent = null;
    }


    /**
     * 格式化代码
     * @param theElement
     */
//    public static void reformatJavaFile(PsiElement theElement) {
//        CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(theElement.getProject());
//        try {
//            codeStyleManager.reformat(theElement);
//        } catch (Exception e) {
//            LOGGER.error("reformat code failed", e);
//        }
//    }
}