package com.jdc.diffverificate.service;

import com.intellij.openapi.project.Project;
import com.jdc.diffverificate.AnnotationSettingsState;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

public class URLUtils {
    public static final String antnUserName = "dev@zy.com";
    public static final String antnPassword = "123456";
    public static final String antnOauthToken = "";

    private static String antnUrl = "https://eistest.unishining.com/prompt-dev-api";
    private static String antnLogin = "/api/login";
    private static String antnCode = "/api/annotationCode";
    private static String antnLogout = "/api/logout/";
    private static String antnPoints = "/api/annotationOptions";

    public static AntnConfigPersistent getAntnPersistent(Project project) {
        AntnConfigPersistent config = AntnConfigPersistent.getInstance(project);
        if (StringUtils.isBlank(config.getAntnUserName())) {
            config.setAntnUserName(antnUserName);
        }
        if (StringUtils.isBlank(config.getAntnPassword())) {
            config.setAntnPassword(antnPassword);
        }
        return config;
    }


    public static void persistentAntnToken(Project project, String token) {
        AntnConfigPersistent config = AntnConfigPersistent.getInstance(project);
        if (StringUtils.isBlank(token)) {
            return;
        }
        config.setAntnOauthToken(token);
    }
    public static void persistentAntnUser(Project project, String username, String password) {
        AntnConfigPersistent config = AntnConfigPersistent.getInstance(project);
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return;
        }
        config.setAntnUserName(username);
        config.setAntnPassword(password);
    }

    public static void persistentAntnOauth(Project project, String username, String password, String oauthToken) {
        AntnConfigPersistent config = AntnConfigPersistent.getInstance(project);
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password) || StringUtils.isBlank(oauthToken)) {
            return;
        }
        config.setAntnUserName(username);
        config.setAntnPassword(password);
        config.setAntnOauthToken(oauthToken);
    }

    public static String getOauthToken(Project project) {
        AntnConfigPersistent config = AntnConfigPersistent.getInstance(project);
        if (ObjectUtils.isEmpty(config)) return "";

        return config.getAntnOauthToken();
    }

    public static String getAntnUrl() {
        return antnUrl;
    }

    public static String getAntnLogin() {
        return getAntnUrl() + antnLogin;
    }
    public static String getAntnCode() {
        return getAntnUrl() + antnCode;
    }
    public static String getAntnLogout() {
        return getAntnUrl() + antnLogout;
    }

    public static String getAntnPoints() {
        return getAntnUrl() + antnPoints;
    }



}
