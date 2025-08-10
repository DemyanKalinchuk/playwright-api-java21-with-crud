package utils.request.path;

import lombok.Getter;

/**
 * Central place for all API endpoints.
 * Use `%s` in URLs for path parameters; HttpRequest replaces them left-to-right.
 */
public enum WorkPath implements IPath {

    // ===== Auth
    LOGIN("/api/login", "Login with credentials"),
    PASSWORD_RESET("/api/password/reset", "Request password reset"),

    // ===== Users
    USERS_ROOT("/api/users", "Users collection (create/list)"),
    USER_BY_ID("/api/users/%s", "User by id"),

    // ===== Posts (example: JSONPlaceholder-like)
    POSTS_ROOT("/posts", "Posts collection"),
    POST_BY_ID("/posts/%s", "Post by id");

    private final String url;
    @Getter
    private final String description;

    WorkPath(String url, String description) {
        this.url = url;
        this.description = description;
    }

    @Override
    public String url() {
        return url;
    }
}

