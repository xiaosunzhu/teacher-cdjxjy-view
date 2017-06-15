package net.sunyijun.tool.app.teacher.cdjxjy;


import net.sunyijun.resource.config.IConfigKey;

public enum IConfigs implements IConfigKey {
    USER_NAME("username"),
    PASSWORD("password");

    private final String key;

    IConfigs(String key) {
        this.key = key;
    }

    public String getKeyString() {
        return key;
    }
}
