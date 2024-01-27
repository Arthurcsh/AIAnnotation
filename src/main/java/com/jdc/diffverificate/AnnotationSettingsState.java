package com.jdc.diffverificate;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.jdc.diffverificate.utils.XMLSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.project.Project;

@State(
        name = "com.jdc.diffverificate.AnnotationSettingsState",
        storages = @Storage("AnnotationSettingsPlugin.xml")
)
public class AnnotationSettingsState implements PersistentStateComponent<AnnotationSettingsState> {

    private int gptType = 1;
    private int annoteLanguage = 1;
    private String codeLanguage = "";
    public String getCodeLanguage() {
        return codeLanguage;
    }

    public void setCodeLanguage(String codeLanguage) {
        this.codeLanguage = codeLanguage;
    }

    public int getGptType() {
        return gptType;
    }

    public void setGptType(int gptType) {
        this.gptType = gptType;
    }

    public int getAnnoteLanguage() {
        return annoteLanguage;
    }

    public void setAnnoteLanguage(int annoteLanguage) {
        this.annoteLanguage = annoteLanguage;
    }

    public static AnnotationSettingsState getInstance(Project project) {
        return project.getService(AnnotationSettingsState.class);
    }

    public AnnotationSettingsState() {

        initSettings();
    }

    private void initSettings() {

    }

    @Override
    public @Nullable AnnotationSettingsState getState() {
        return this;
    }

    /**
     * 新的组件状态被加载时，调用该方法，如果IDE运行期间，保存数据的文件被从外部修改，则该方法会被再次调用
     *
     * @param state
     */
    @Override
    public void loadState(@NotNull AnnotationSettingsState state) {
        XMLSerializerUtil.copyBean(state, this);
    }
}
