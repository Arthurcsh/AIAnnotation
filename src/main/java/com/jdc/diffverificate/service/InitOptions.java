package com.jdc.diffverificate.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class InitOptions {
    /**
     * 主干分支名称
     */
    private String masterBranch;

    /**
     * 发布分支名称
     */
    private String releaseBranch;

    /**
     * 测试分支名称
     */
    private String testBranch;

    /**
     * 开发分支前缀
     */
    private String featurePrefix;

    /**
     * 修复分支前缀
     */
    private String hotfixPrefix;

    /**
     * 版本前缀
     */
    private String tagPrefix;

    /**
     * 发布完成是否删除发布分支
     */
    private boolean releaseFinishIsDeleteRelease;

    /**
     * 发布完成是否删除开发分支
     */
    private boolean releaseFinishIsDeleteFeature;

    /**
     * 登录用户
     */
    private String userToken;
    private transient String kUsername;
    private transient String kPassword;

    /**
     * 语言
     */
    private LanguageEnum language;

    public String getMasterBranch() {
        return masterBranch;
    }

    public void setMasterBranch(String masterBranch) {
        this.masterBranch = masterBranch;
    }

    public String getReleaseBranch() {
        return releaseBranch;
    }

    public void setReleaseBranch(String releaseBranch) {
        this.releaseBranch = releaseBranch;
    }

    public String getTestBranch() {
        return testBranch;
    }

    public void setTestBranch(String testBranch) {
        this.testBranch = testBranch;
    }

    public String getFeaturePrefix() {
        return featurePrefix;
    }

    public void setFeaturePrefix(String featurePrefix) {
        this.featurePrefix = featurePrefix;
    }

    public String getHotfixPrefix() {
        return hotfixPrefix;
    }

    public void setHotfixPrefix(String hotfixPrefix) {
        this.hotfixPrefix = hotfixPrefix;
    }

    public String getTagPrefix() {
        return tagPrefix;
    }

    public void setTagPrefix(String tagPrefix) {
        this.tagPrefix = tagPrefix;
    }

    public boolean isReleaseFinishIsDeleteRelease() {
        return releaseFinishIsDeleteRelease;
    }

    public void setReleaseFinishIsDeleteRelease(boolean releaseFinishIsDeleteRelease) {
        this.releaseFinishIsDeleteRelease = releaseFinishIsDeleteRelease;
    }

    public boolean isReleaseFinishIsDeleteFeature() {
        return releaseFinishIsDeleteFeature;
    }

    public void setReleaseFinishIsDeleteFeature(boolean releaseFinishIsDeleteFeature) {
        this.releaseFinishIsDeleteFeature = releaseFinishIsDeleteFeature;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getKUsername() {
        return kUsername;
    }

    public void setKUsername(String kUsername) {
        this.kUsername = kUsername;
    }

    public String getKPassword() {
        return kPassword;
    }

    public void setKPassword(String kPassword) {
        this.kPassword = kPassword;
    }

    public LanguageEnum getLanguage() {
        return Objects.isNull(language) ? LanguageEnum.CN : language;
    }

    public void setLanguage(LanguageEnum language) {
        this.language = language;
    }
}


 enum LanguageEnum {
    CN("中文", Locale.CHINESE),
    EN("English", Locale.ENGLISH);

    private final String language;
    private final Locale locale;

    LanguageEnum(String language, Locale locale) {
        this.language = language;
        this.locale = locale;
    }

    public static LanguageEnum getByLanguage(String language) {
        for (LanguageEnum anEnum : values()) {
            if (anEnum.getLanguage().equals(language)) {
                return anEnum;
            }
        }
        return CN;
    }

    public Locale getLocale() {
        return locale;
    }

    public static List<String> getAllLanguage() {
        List<String> languages = new ArrayList<>();
        for (LanguageEnum anEnum : values()) {
            languages.add(anEnum.getLanguage());
        }
        return languages;
    }

    public String getLanguage() {
        return language;
    }

}