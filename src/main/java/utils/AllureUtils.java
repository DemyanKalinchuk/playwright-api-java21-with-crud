package utils;

import com.microsoft.playwright.APIResponse;
import io.qameta.allure.Allure;
import io.restassured.response.Response;
import utils.json.JsonUtils;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class AllureUtils {
    private AllureUtils() {
        // utility class
    }

    // ---------- Generic attachments ----------

    public static void attachJson(String attachmentName, byte[] jsonBytes) {
        Allure.addAttachment(
                attachmentName + " (json)",
                "application/json",
                new ByteArrayInputStream(jsonBytes),
                ".json"
        );
    }

    public static void attachJson(String attachmentName, String jsonText) {
        Allure.addAttachment(
                attachmentName + " (json)",
                "application/json",
                new ByteArrayInputStream(jsonText.getBytes(StandardCharsets.UTF_8)),
                ".json"
        );
    }

    public static void attachText(String attachmentName, String text) {
        Allure.addAttachment(
                attachmentName + " (text)",
                "text/plain",
                new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)),
                ".txt"
        );
    }

    public static void attachKeyValues(String attachmentName, Map<?, ?> keyValues) {
        StringBuilder builder = new StringBuilder();
        keyValues.forEach((key, value) ->
                builder.append(key).append(": ").append(String.valueOf(value)).append('\n'));
        attachText(attachmentName, builder.toString());
    }

    // ---------- Playwright APIResponse attachment ----------

    public static void attachResponse(String attachmentName, APIResponse apiResponse) {
        // Meta (status + headers)
        StringBuilder metaBuilder = new StringBuilder()
                .append("URL: ").append(apiResponse.url()).append('\n')
                .append("Status: ").append(apiResponse.status()).append(' ')
                .append(apiResponse.statusText()).append("\n\nHeaders:\n");

        apiResponse.headers().forEach((headerName, headerValue) ->
                metaBuilder.append(headerName).append(": ").append(headerValue).append('\n'));

        attachText(attachmentName + " META", metaBuilder.toString());

        // Try pretty JSON; fall back to raw body
        try {
            var parsed = JsonUtils.readJson(apiResponse.body());
            attachJson(attachmentName + " BODY", JsonUtils.toJson(parsed));
        } catch (Exception ex) {
            attachText(attachmentName + " BODY (raw)",
                    new String(apiResponse.body(), StandardCharsets.UTF_8));
        }
    }

    // ---------- RestAssured Response formatting for Allure ----------

    public static String getAllureReportMessage(Response httpResponse,
                                                String responseBody,
                                                Object requestBody,
                                                String pathOrUrl) {

        String requestDate = httpResponse.getHeader("Date");
        String requestDurationMillis = String.valueOf(httpResponse.getTime());
        String statusCode = String.valueOf(httpResponse.getStatusCode());
        String prettyRequestBody = (requestBody != null) ? requestBody.toString() : "";

        String template = "Path: %s" +
                "\n\nRequest date: %s" +
                "\n\nTime request (ms): %s" +
                "\n\nRequest body: %s" +
                "\n\nResponse status code: %s" +
                "\n\nResponse: %s";

        return String.format(
                template,
                pathOrUrl,
                requestDate,
                requestDurationMillis,
                prettyRequestBody,
                statusCode,
                responseBody
        );
    }

    public static void addAttachmentToReport(String title, String content) {
        Allure.addAttachment(title, content);
    }
}