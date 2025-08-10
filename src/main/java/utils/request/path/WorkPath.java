package utils.request.path;

import lombok.Getter;

public enum WorkPath implements IPath {

    // ===== Users (ReqRes)
    USERS_ROOT("/api/users",
            "Users collection (create/list)"),
    USER_BY_ID("/api/users/%s",
            "User by id"),

    // ===== Posts (JSONPlaceholder)
    POSTS_ROOT("/posts",
            "Posts collection"),
    POST_BY_ID("/posts/%s",
            "Post by id"),

    // ===== Auth
    LOGIN("/api/login",
            "Login"),
    REGISTER("/api/register",
            "Register");

    private final String url;
    @Getter private final String description;

    WorkPath(String url, String description) { this.url = url; this.description = description; }
    @Override public String url() { return url; }
}

