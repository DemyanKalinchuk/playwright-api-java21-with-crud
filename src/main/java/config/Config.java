package config;

public final class Config {
    private Config() {}
    private static String get(String k, String def) {
        String env = System.getenv(k);
        if (env != null && !env.isBlank()) return env;
        String prop = System.getProperty(k);
        return (prop != null && !prop.isBlank()) ? prop : def;
    }
    public static String baseApiUrl()     { return get("BASE_URL", "https://reqres.in"); }
    public static String baseFilesApiUrl(){ return get("FILES_BASE_URL", baseApiUrl()); }
    public static boolean consoleLog()    { return Boolean.parseBoolean(get("API_CONSOLE_LOG", "false")); }
    public static int retryMax()          { return Integer.parseInt(get("RETRY_MAX", "2")); }
    public static int timeoutMs()         { return Integer.parseInt(get("TIMEOUT_MS", "30000")); }
    public static String defaultLang()    { return get("ACCEPT_LANGUAGE", "en-US"); }
    public static String bearer()         { return get("API_TOKEN", ""); }
}
