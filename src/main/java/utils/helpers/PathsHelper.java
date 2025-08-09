package utils.helpers;

public final class PathsHelper {
    private PathsHelper() {}

    public static final class ReqRes {
        public static String users() { return "/api/users"; }
        public static String user(int id) { return "/api/users/" + id; }
    }

    public static final class JsonPh {
        public static String posts() { return "/posts"; }
        public static String post(int id) { return "/posts/" + id; }
    }
}

