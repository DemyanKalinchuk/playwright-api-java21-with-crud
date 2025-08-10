package api.steps;

import api.builders.BodyBuilder;
import api.pojo.post.Post;
import api.pojo.users.User;
import utils.json.Json;
import utils.request.Headers;
import utils.request.HttpRequest;

import static utils.base.Base.testCred;
import static utils.json.Json.getAsText;
import static utils.request.path.WorkPath.*;

public class UseApiSteps {
    private final HttpRequest http;

    public UseApiSteps() { this.http = new HttpRequest(); }
    private final BodyBuilder bodyBuilder = new BodyBuilder();
    public UseApiSteps(String baseUrl) { this.http = new HttpRequest().configure(baseUrl, null, config.Config.consoleLog()); }



    // ---- Users (ReqRes)
    public String createUser(String first, String last, String email, String job) {
        User payload = bodyBuilder.buildNewUser(first, last, email, job);
        return http.postRequest(new Headers().addAuthHeader(testCred.baseApiToken()), payload, USERS_ROOT);
    }
    public String createUserAndGetId(String first, String last, String email, String job) {
        String json = createUser(first, last, email, job);
        return Json.getAsText(json, "id");
    }
    public String getUser(String id, String token) {
        return http.getRequest(new Headers().addAuthHeader(token), USER_BY_ID, id);
    }
    public String updateUser(String id, String first, String last, String email, String job, String token) {
        var address = bodyBuilder.buildAddress("Main", "Apt 2", "Zion", "00001");
        User payload = bodyBuilder.buildFullUser(id, first, last, email, job, address);
        return http.putRequest(new Headers().addAuthHeader(token), payload, USER_BY_ID, id);
    }
    public String deleteUser(String id) {
        return http.deleteRequest(new Headers().addAuthHeader(testCred.baseApiToken()), USER_BY_ID, id);
    }

    // Cred's
    public String login(String email, String password) {
        var body = bodyBuilder.buildAuthRequest(email, password);
        return http.postRequest(new Headers().addAuthHeader(testCred.baseApiToken()), body, LOGIN);
    }

    public String register(String email, String password) {
        var body = bodyBuilder.buildAuthRequest(email, password);
        return http.postRequest(new Headers().addAuthHeader(testCred.baseApiToken()), body, REGISTER);
    }

}
