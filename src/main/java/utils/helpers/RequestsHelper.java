package utils.helpers;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import utils.json.JsonUtils;

import java.io.IOException;
import java.util.Map;

import static utils.AllureUtils.*;

public final class RequestsHelper {
    private RequestsHelper() {}

    // ---------- GET ----------
    public static APIResponse get(APIRequestContext api, String path) {
        attachText("REQUEST GET " + path, "No query params");
        APIResponse res = api.get(path);
        attachResponse("RESPONSE GET " + path, res);
        return res;
    }

    public static APIResponse get(APIRequestContext api, String path, Map<String, ?> query) {
        attachKv("REQUEST GET " + path + " | query", query);
        APIResponse res = api.get(path, HttpHelper.query(query));
        attachResponse("RESPONSE GET " + path, res);
        return res;
    }

    public static APIResponse getAuth(APIRequestContext api, String path, String token) {
        attachText("REQUEST GET " + path + " | headers", "Authorization: Bearer " + token);
        APIResponse res = api.get(path, RequestOptions.create().setHeader("Authorization", "Bearer " + token));
        attachResponse("RESPONSE GET " + path, res);
        return res;
    }

    public static APIResponse getAuth(APIRequestContext api, String path, Map<String, ?> query, String token) {
        var ro = HttpHelper.query(query);
        ro.setHeader("Authorization", "Bearer " + token);
        attachKv("REQUEST GET " + path + " | query", query);
        attachText("REQUEST GET " + path + " | headers", "Authorization: Bearer " + token);
        APIResponse res = api.get(path, ro);
        attachResponse("RESPONSE GET " + path, res);
        return res;
    }

    // ---------- JSON BODY VERBS ----------
    public static APIResponse postJson(APIRequestContext api, String path, Object body) throws IOException {
        String json = JsonUtils.toJson(body);
        attachJson("REQUEST POST " + path + " BODY", json);
        APIResponse res = api.post(path, HttpHelper.json(body));
        attachResponse("RESPONSE POST " + path, res);
        return res;
    }

    public static APIResponse putJson(APIRequestContext api, String path, Object body) throws IOException {
        String json = JsonUtils.toJson(body);
        attachJson("REQUEST PUT " + path + " BODY", json);
        APIResponse res = api.put(path, HttpHelper.json(body));
        attachResponse("RESPONSE PUT " + path, res);
        return res;
    }

    public static APIResponse patchJson(APIRequestContext api, String path, Object body) throws IOException {
        String json = JsonUtils.toJson(body);
        attachJson("REQUEST PATCH " + path + " BODY", json);
        APIResponse res = api.patch(path, HttpHelper.json(body));
        attachResponse("RESPONSE PATCH " + path, res);
        return res;
    }

    // ---------- DELETE ----------
    public static APIResponse delete(APIRequestContext api, String path) {
        attachText("REQUEST DELETE " + path, "(no body)");
        APIResponse res = api.delete(path);
        attachResponse("RESPONSE DELETE " + path, res);
        return res;
    }
}