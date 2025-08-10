package utils;

import com.microsoft.playwright.APIResponse;
import io.qameta.allure.Allure;
import io.restassured.response.Response;
import utils.json.JsonUtils;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class AllureUtils {
    private AllureUtils() {}

    public static void attachJson(String name, byte[] body) {
        Allure.addAttachment(name + " (json)", "application/json",
                new ByteArrayInputStream(body), ".json");
    }

    public static void attachJson(String name, String json) {
        Allure.addAttachment(name + " (json)", "application/json",
                new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), ".json");
    }

    public static void attachText(String name, String text) {
        Allure.addAttachment(name + " (text)", "text/plain",
                new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)), ".txt");
    }

    public static void attachKv(String name, Map<?, ?> kv) {
        StringBuilder sb = new StringBuilder();
        kv.forEach((k,v) -> sb.append(k).append(": ").append(String.valueOf(v)).append('\n'));
        attachText(name, sb.toString());
    }

    public static void attachResponse(String name, APIResponse res) {
        // Meta (status + headers)
        StringBuilder meta = new StringBuilder()
                .append("URL: ").append(res.url()).append('\n')
                .append("Status: ").append(res.status()).append(' ').append(res.statusText()).append("\n\nHeaders:\n");
        res.headers().forEach((k,v) -> meta.append(k).append(": ").append(v).append('\n'));
        attachText(name + " META", meta.toString());

        // Try JSON pretty; fall back to raw
        try {
            var map = JsonUtils.readJson(res.body());
            attachJson(name + " BODY", JsonUtils.toJson(map));
        } catch (Exception e) {
            attachText(name + " BODY (raw)", new String(res.body(), StandardCharsets.UTF_8));
        }
    }

    public static String getAllureReportMessage(Response resp, String response, Object body, String path) {

        String request_date = resp.getHeader("Date");
        String timeRequest = String.valueOf(resp.getTime());
        String responseStatus = String.valueOf(resp.getStatusCode());
        String prettyRequest = body != null ? body.toString() : "";

        String allure_report = "Path: %s" +
                "\n\nRequest date: %s" +
                "\n\nTime request: %s" +
                "\n\nRequest body: %s" +
                "\n\nResponse status code: %s" +
                "\n\nResponse: %s";

        allure_report = String.format(allure_report, path, request_date, timeRequest, prettyRequest, responseStatus,
                response);
        return allure_report;
    }

    public static void addAttachmentToReport(String title, String value) {
        Allure.addAttachment(title, value);
    }
}
