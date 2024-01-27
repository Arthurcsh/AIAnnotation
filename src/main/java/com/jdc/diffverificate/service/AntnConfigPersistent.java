package com.jdc.diffverificate.service;


import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.jdc.diffverificate.utils.XMLSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "com.jdc.diffverificate.AnnotationUserConfig",
        storages = @Storage("AnnotationUserConfig.xml")
)
public class AntnConfigPersistent implements PersistentStateComponent<AntnConfigPersistent>  {

    private String antnUserName = "";
    private String antnPassword = "";
    private String antnOauthToken = "";

    public String getAntnUserName() {
        return antnUserName;
    }

    public void setAntnUserName(String antnUserName) {
        this.antnUserName = antnUserName;
    }

    public String getAntnPassword() { return antnPassword; }

    public void setAntnPassword(String antnPassword) {
        this.antnPassword = antnPassword;
    }

    public String getAntnOauthToken() {
        return antnOauthToken;
    }

    public void setAntnOauthToken(String antnToken) {
        this.antnOauthToken = antnToken;
    }


    public static AntnConfigPersistent getInstance(Project project) {
        return project.getService(AntnConfigPersistent.class);
    }

    public AntnConfigPersistent() {
        initConfigPersisten();
    }

    private void initConfigPersisten() {

    }

    public @Nullable AntnConfigPersistent getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull AntnConfigPersistent state) {
        XMLSerializerUtil.copyBean(state, this);
    }

}
