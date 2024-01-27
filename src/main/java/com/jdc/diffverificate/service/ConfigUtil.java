package com.jdc.diffverificate.service;

import com.google.gson.JsonObject;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.jdc.diffverificate.HttpClientUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.prefs.Preferences;

public class ConfigUtil {
    private static final Logger LOG = Logger.getInstance(ConfigUtil.class);
    private static final Preferences PREFERENCES = Preferences.userRoot().node("com.jdc.annotation");
    private static final Map<String, InitOptions> map = new ConcurrentHashMap<>();

    public static Pair<String, String> getBaiduConfig() {
        return new Pair<>(PREFERENCES.get("antnAppId", ""), PREFERENCES.get("antnAppKey", ""));
    }

    public static void saveAnnotationConfig(String appId, String appKey) {
        PREFERENCES.put("antnAppId", appId);
        PREFERENCES.put("antnAppKey", appKey);
    }

    public static Pair<String, String> getKUser() {
        return new Pair<>(PREFERENCES.get("kUsername", ""), PREFERENCES.get("kPassword", ""));
    }

    public static void saveLoginUser(String name, String pwd) {
        PREFERENCES.put("kUsername", name);
        PREFERENCES.put("kPassword", pwd);
    }

    public static String getOauthToken() {
        return PREFERENCES.get("oauthToken", "abcd");
    }

    public static void saveOauthToken(String token) {
        PREFERENCES.put("kOauthToken", token);
    }

    /**
     * 将配置存储到本地项目空间
     *
     * @param project    project
     * @param configJson configJson
     */
    public static void saveConfigToLocal(Project project, String configJson) {
        // 存储到本地项目空间
        PropertiesComponent component = PropertiesComponent.getInstance(project);
        component.setValue(Constants.KEY_PREFIX + project.getName(), configJson);
        map.remove(project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME);
    }

    /**
     * 将配置存储到本地文件
     *
     * @param project    project
     * @param configJson configJson
     */
    public static void saveConfigToFile(Project project, String configJson) {
        String filePath = project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME;
        File file = new File(filePath);
        try {
            FileUtils.write(file, configJson, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        map.remove(project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME);
    }

    /**
     * 判断插件否初始化
     *
     * @param project project
     * @return boolean 已初始化返回true，否则返回false
     */
    public static boolean isInit(Project project) {
        if (project == null) {
            return false;
        }
        return getConfig(project).isPresent();
    }

    public static void tryInitConfig(Project project) {
        InitOptions options = getFromProjectConfigFile(project);
        if (Objects.isNull(options)) {
            options = getFromProjectWorkspace(project);
        }

        if (Objects.nonNull(options)) {
            Pair<String, String> pair = getKUser();
            options.setKUsername(pair.getFirst());
            options.setKPassword(pair.getSecond());
            map.put(project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME, options);
        }
    }

    public static @NotNull InitOptions getInitOptions(Project project) {
        return map.get(project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME);
    }

    /**
     * 获取配置
     *
     * @param project project
     * @return InitOptions
     */
    public static Optional<InitOptions> getConfig(@NotNull Project project) {
        InitOptions initOptions = map.get(project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME);
        return Optional.ofNullable(initOptions);
    }

    /**
     * 从本地项目空间获取配置
     *
     * @param project project
     * @return InitOptions
     */
    private static InitOptions getFromProjectWorkspace(@NotNull Project project) {
        PropertiesComponent component = PropertiesComponent.getInstance(project);
        String key = Constants.KEY_PREFIX + project.getName();
        String json = component.getValue(key);
        if (StringUtils.isNotBlank(json)) {
            LOG.info("完成读取项目空间配置workspace.xml,key:" + key);
            return HttpClientUtil.gson.fromJson(json, InitOptions.class);
        }
        return null;
    }

    /**
     * 从配置文件获取配置
     *
     * @param project project
     * @return InitOptions
     */
    private static InitOptions getFromProjectConfigFile(Project project) {
        String filePath = project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return null;
            }
            String config = FileUtils.readFileToString(file, StandardCharsets.UTF_8.name());
            LOG.info("完成读取配置文件:" + filePath);
            return HttpClientUtil.gson.fromJson(config, InitOptions.class);
        } catch (Exception e) {
//            NotifyUtil.notifyError(project, "读取" + filePath + "错误:" + e.getClass().getSimpleName() + "," + e.getMessage());
            return null;
        }
    }

    public static JsonObject getProjectConfigFromFile(Project project) {
        try {
            String filePath = project.getBasePath() + File.separator + Constants.CONFIG_FILE_NAME_PROJECT;
            File file = new File(filePath);
            if (!file.exists()) {
                return null;
            }
            String config = FileUtils.readFileToString(file, StandardCharsets.UTF_8.name());
            return HttpClientUtil.gson.fromJson(config, JsonObject.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
