package config;

import static utils.base.Base.testCred;

public final class Config {
    private static String get(String key, String defaultValue) {
        String envValue = System.getenv(key);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        String systemPropertyValue = System.getProperty(key);
        if (systemPropertyValue != null && !systemPropertyValue.isBlank()) {
            return systemPropertyValue;
        }

        return defaultValue;
    }
    public static String baseApiUrl()      { return get("BASE_URL", testCred.baseApiUrl()); }
    public static String baseFilesApiUrl() { return get("FILES_BASE_URL", baseApiUrl()); }
    public static String bearer()          { return get("API_TOKEN", testCred.baseApiToken()); }
    public static String acceptLang()      { return get("ACCEPT_LANGUAGE", "en-US"); }
    public static boolean consoleLog()     { return Boolean.parseBoolean(get("API_CONSOLE_LOG", "false")); }
    public static int retryMax()           { return Integer.parseInt(get("RETRY_MAX", "2")); }
}
