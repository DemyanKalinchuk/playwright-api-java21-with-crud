package api.steps;

import api.builders.BodyBuilder;
import api.pojo.post.Post;
import api.pojo.users.User;
import utils.request.Headers;
import utils.request.HttpRequest;

import static utils.base.Base.testCred;
import static utils.json.Json.getAsText;
import static utils.request.path.WorkPath.*;

public class UseApiSteps {
    private final HttpRequest http;

    public UseApiSteps() { this.http = new HttpRequest(); }
    private final BodyBuilder bodyBuilder = new BodyBuilder();

    // ===== Users
    public String createUser(String first, String last, String email, String job) {
        User body = bodyBuilder.buildNewUser(first, last, email, job);
        return http.postRequest(new Headers().addAuthHeader(testCred.baseApiToken()), body, USERS_ROOT);
    }

    /** Convenience: create user and return its id (safe JSON parsing) */
    public String createUserAndGetId(String first, String last, String email, String job) {
        String body = createUser(first, last, email, job); // returns String
        return getAsText(body, "id");
    }

    public String getUser(String id, String token) {
        return http.getRequest(new Headers().addAuthHeader(token), USER_BY_ID, id);
    }

    public String updateUser(String id, String first, String last, String email, String job) {
        var address = bodyBuilder.buildAddress("Main", "Apt 2", "Zion", "00001");
        User body = bodyBuilder.buildFullUser(id, first, last, email, job, address);
        return http.putRequest(new Headers().addAuthHeader(testCred.baseApiToken()), body, USER_BY_ID, id);
    }

    public String deleteUser(String id) {
        return http.deleteRequest(new Headers().addAuthHeader(testCred.baseApiToken()), USER_BY_ID, id);
    }

    // ===== Posts
    public String createPost(Integer userId, String title, String body, String token) {
        Post payload = bodyBuilder.buildPost(userId, title, body);
        return http.postRequest(new Headers().addAuthHeader(token), payload, POSTS_ROOT);
    }

    /** Convenience: create post and return its id (works when API returns numeric id) */
    public String createPostAndGetId(Integer userId, String title, String body, String token) {
        String resp = createPost(userId, title, body, token);
        return getAsText(resp, "id");
    }

    public String getPost(String id) {
        return http.getRequest(new Headers().addAuthHeader(testCred.baseApiToken()), POST_BY_ID, id);
    }

    public String updatePostPut(String id, Integer userId, String title, String body) {
        Post payload = bodyBuilder.buildPost(userId, title, body);
        return http.putRequest(new Headers().addAuthHeader(testCred.baseApiToken()), payload, POST_BY_ID, id);
    }

    public String updatePostPatch(String id, String title) {
        var partial = java.util.Map.of("title", title);
        return http.putRequest(new Headers().addAuthHeader(testCred.baseApiToken()), partial, POST_BY_ID, id); // swap to PATCH when added
    }

    public String deletePost(String id) {
        return http.deleteRequest(new Headers().addAuthHeader(testCred.baseApiToken()), POST_BY_ID, id);
    }
}
