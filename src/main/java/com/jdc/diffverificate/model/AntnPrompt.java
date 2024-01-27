package com.jdc.diffverificate.model;

public class AntnPrompt {

    private ModeType gptType;
    private AntnLang antnLang;
    private CodeLang codeLang;
    private String prompt;

}

enum ModeType {
    GPT4("gpt-4", "GPT-4"),
    GPT3("gpt-3.5-turbo-16k", "GPT-3.5");

    private String gptKey;
    private String gptValue;

    // 构造方法
    ModeType( String gptKey, String gptValue) {
        this.gptKey = gptKey;
        this.gptValue = gptValue;
    }
}
enum AntnLang {
    LANG_ZN("Chinese", "中文"),
    LANG_EN("English", "英文");

    private String langKey;
    private String langValue;

    // 构造方法
    AntnLang( String langKey, String langValue) {
        this.langKey = langKey;
        this.langValue = langValue;
    }
}

enum CodeLang {
    CODE_JAVA("lang_java", "Java"),
    CODE_CPLUS("lang_cplus", "C++"),
    CODE_PYTHON("lang_python", "Python"),
    CODE_JS("lang_js", "Javascript"),
    CODE_CSS("lang_css", "CSS"),
    CODE_TS("lang_typescript", "Typescript"),
    CODE_OC("lang_objc", "Objective-C"),
    CODE_SWIFT("lang_swift", "Swift");

    private String codeKey;
    private String codeValue;

    // 构造方法
    CodeLang( String codeKey, String codeValue) {
        this.codeKey = codeKey;
        this.codeValue = codeValue;
    }
}

